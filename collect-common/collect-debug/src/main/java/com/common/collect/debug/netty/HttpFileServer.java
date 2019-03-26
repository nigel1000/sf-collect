package com.common.collect.debug.netty;

import com.google.common.base.Throwables;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.URLDecoder;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class HttpFileServer {

    public void run(final int port, final String baseDir) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) {
                            //首先向ChannelPipeline中添加HTTP请求消息解码器，
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            //添加了HttpObjectAggregator解码器，
                            //它的作用是将多个消息转换为单一的FullHttpRequest或者FullHttpResponse，
                            //原因是HTTP解码器在每个HTTP消息中会生成多个消息对象。
                            //（1）HttpRequest / HttpResponse；
                            //（2）HttpContent；
                            //（3）LastHttpContent。
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            //新增HTTP响应编码器，对HTTP响应消息进行编码；
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            //新增Chunked handler，它的主要作用是支持异步发送大的码流（例如大的文件传输），
                            //但不占用过多的内存，防止发生Java内存溢出错误。
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            //添加HttpFileServerHandler，用于文件服务器的业务逻辑处理。
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(baseDir));
                        }
                    });
            InetAddress address = InetAddress.getLocalHost();
            // 获取IP地址
            String ip = address.getHostAddress();
            ChannelFuture future = b.bind(ip, port).sync();
            log.info("HTTP文件目录服务器启动,网址是 http://{}:{} ,基本目录：{}", ip, port, baseDir);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static class HttpFileServerHandler extends SimpleChannelInboundHandler {

        private final String baseDir;

        public HttpFileServerHandler(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, Object o) throws Exception {
            FullHttpRequest request = (FullHttpRequest) o;
            //首先对HTTP请求消息的解码结果进行判断，如果解码失败，直接构造HTTP 400错误返回。
            if (!request.getDecoderResult().isSuccess()) {
                sendError(ctx, "request 解码失败");
                return;
            }
            //对请求行中的方法进行判断，如果不是从浏览器或者表单设置为GET发起的请求（例如POST），则构造HTTP 405错误返回。
            if (request.getMethod() != GET) {
                sendError(ctx, "request 只接受 GET");
                return;
            }
            //对请求URL进行包装
            String uri = request.getUri();
            //如果构造的URI不合法，则返回HTTP 403错误。
            if (uri == null) {
                sendError(ctx, "uri 不合法");
                return;
            }
            //使用新组装的URI路径构造File对象。
            String filePath = baseDir + URLDecoder.decode(uri, "UTF-8");
            File file = new File(filePath);
            if (!file.exists()) {
                sendError(ctx, filePath + " 不存在");
                return;
            }
            //如果文件是目录，则发送目录的链接给客户端浏览器。
            if (file.isDirectory()) {
                if (uri.endsWith("/")) {
                    sendDirList(ctx, file);
                } else {
                    sendRedirect(ctx, uri + '/');
                }
                return;
            }
            //如果用户在浏览器上点击超链接直接打开或者下载文件
            //对超链接的文件进行合法性判断，如果不是合法文件，则返回HTTP 403错误。
            if (!file.isFile()) {
                sendError(ctx, "不是文件不能进行下载");
                return;
            }
            //使用随机文件读写类以只读的方式打开文件，如果文件打开失败，则返回HTTP 404错误。
            RandomAccessFile randomAccessFile;
            try {
                randomAccessFile = new RandomAccessFile(file, "r");// 以只读的方式打开文件
            } catch (FileNotFoundException ex) {
                sendError(ctx, Throwables.getStackTraceAsString(ex));
                return;
            }
            //获取文件的长度，构造成功的HTTP应答消息
            long fileLength = randomAccessFile.length();
            //在消息头中设置content length和content type
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            setContentLength(response, fileLength);
            setContentTypeHeader(response, file);
            //判断是否是Keep-Alive，如果是，则在应答消息头中设置Connection为Keep-Alive。
            if (isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            //发送响应消息。
            ctx.write(response);
            ChannelFuture sendFileFuture;
            //通过Netty的ChunkedFile对象直接将文件写入到发送缓冲区中。
            sendFileFuture = ctx.write(
                    new ChunkedFile(randomAccessFile, 0, fileLength, 8192),
                    ctx.newProgressivePromise());
            //最后为sendFileFuture增加GenericFutureListener，如果发送完成，打印“Transfer complete.”。
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) {
                    if (total < 0) { // total unknown
                        log.info("Transfer progress: {}", progress);
                    } else {
                        log.info("Transfer progress:{}/{}", progress, total);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) {
                    log.info("Transfer complete.");
                }
            });
            //如果使用chunked编码，最后需要发送一个编码结束的空消息体，
            //将LastHttpContent的EMPTY_LAST_CONTENT发送到缓冲区中，标识所有的消息体已经发送完成，
            //同时调用flush方法将之前在发送缓冲区的消息刷新到SocketChannel中发送给对方。
            ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            //如果是非Keep-Alive的，最后一包消息发送完成之后，服务端要主动关闭连接。
            if (!isKeepAlive(request)) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            if (ctx.channel().isActive()) {
                sendError(ctx, Throwables.getStackTraceAsString(cause));
            }
        }

        private static void sendDirList(ChannelHandlerContext ctx, File dir) {
            //首先创建成功的HTTP响应消息
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            //随后设置消息头的类型为“text/html; charset=UTF-8”。
            response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
            //用于构造响应消息体，由于需要将响应结果显示在浏览器上，所以采用了HTML的格式。
            StringBuilder buf = new StringBuilder();
            String dirPath = dir.getPath();
            buf.append("<!DOCTYPE HTML>\r\n");
            buf.append("<HTML><HEAD><TITLE>");
            buf.append(dirPath);
            buf.append(" 目录：");
            buf.append("</TITLE></HEAD><BODY>");
            buf.append("<H3>");
            buf.append(dirPath);
            buf.append(" 目录：");
            buf.append("</H3>\r\n");
            buf.append("<UL>");
            //用于展示根目录下的所有文件和文件夹，同时使用超链接来标识。
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.canRead()) {
                        continue;
                    }
                    String name = f.getName();
                    buf.append("<LI>链接：<a href=\"");
                    buf.append(name);
                    buf.append("\">");
                    buf.append(name);
                    buf.append("</a></LI>\r\n");
                }
            }
            buf.append("</UL></BODY></HTML>\r\n");
            //分配对应消息的缓冲对象
            ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
            //将缓冲区中的响应消息存放到HTTP应答消息中，然后释放缓冲区
            response.content().writeBytes(buffer);
            buffer.release();
            //最后调用writeAndFlush将响应消息发送到缓冲区并刷新到SocketChannel中。
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        private static void sendError(ChannelHandlerContext ctx, String errorMessage) {
            //首先创建成功的HTTP响应消息
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            //随后设置消息头的类型为“text/html; charset=UTF-8”。
            response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
            //用于构造响应消息体，由于需要将响应结果显示在浏览器上，所以采用了HTML的格式。
            StringBuilder buf = new StringBuilder();
            buf.append("<!DOCTYPE HTML>\r\n");
            buf.append("<HTML><HEAD><TITLE>");
            buf.append("错误描述");
            buf.append("</TITLE></HEAD><BODY>");
            buf.append("<H3>");
            buf.append(errorMessage);
            buf.append("</H3>");
            buf.append("</BODY></HTML>\r\n");
            //分配对应消息的缓冲对象
            ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
            //将缓冲区中的响应消息存放到HTTP应答消息中，然后释放缓冲区
            response.content().writeBytes(buffer);
            buffer.release();
            //最后调用writeAndFlush将响应消息发送到缓冲区并刷新到SocketChannel中。
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            response.headers().set(LOCATION, newUri);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        private static void setContentTypeHeader(HttpResponse response, File file) {
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
        }
    }

}