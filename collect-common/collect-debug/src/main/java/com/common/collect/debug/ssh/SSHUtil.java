package com.common.collect.debug.ssh;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.trace.TraceIdUtil;
import com.common.collect.util.FileUtil;
import com.jcraft.jsch.*;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by hznijianfeng on 2018/11/12.
 */

@Slf4j
public class SSHUtil {

    public static byte[] PRIVATE_KEY = null;
    public static byte[] PUBLIC_KEY = null;

    static {
        try {
            PRIVATE_KEY = FileUtil.getBytes(new FileInputStream(System.getProperty("user.home") + "/.ssh/id_rsa"));
            PUBLIC_KEY = FileUtil.getBytes(new FileInputStream(System.getProperty("user.home") + "/.ssh/id_rsa.pub"));
        } catch (FileNotFoundException ex) {
            log.info("找不到秘钥文件", ex);
        }
    }

    //默认session通道存活时间（我这里定义的是5分钟）
    private static Integer SESSION_TIMEOUT = 300000;
    //默认connect通道存活时间
    private static Integer CONNECT_TIMEOUT = 1000;
    //默认端口号
    private static Integer DEFAULT_PORT = 22;

    public static String execCommand(@NonNull SSHInfo sshInfo) {
        log.info("sshInfo:[{}]", sshInfo);
        try {
            Session session = sshInfo.getSession();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            if (channel == null) {
                throw UnifiedException.gen(" 打开 exec 通道失败，需要执行的系统命令：{}", sshInfo.getCmd());
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            channel.setCommand(sshInfo.getCmd());
            channel.setInputStream(null);
            channel.setErrStream(outStream);
            channel.setOutputStream(outStream);
            channel.connect();

            // 读取返回结果
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<String> readFuture = executor.submit(TraceIdUtil.wrap(() -> {
                byte[] result = outStream.toByteArray();
                while (result == null || result.length == 0) {
                    Thread.sleep(500);
                    result = outStream.toByteArray();
                }
                String ret = new String(outStream.toByteArray(), StandardCharsets.UTF_8);
                if (ret.equals("!@#$%^&*()exit!@#$%^&*()")) {
                    return "未返回任何结果，可能是5秒超时，可能本身就没有返回!";
                }
                return ret;
            }));
            executor.execute(TraceIdUtil.wrap(() -> {
                try {
                    // 5秒后若没有主动退出就强制退出
                    Thread.sleep(5000);
                    outStream.write("!@#$%^&*()exit!@#$%^&*()".getBytes(StandardCharsets.UTF_8));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }));
            close(channel, session);
            return readFuture.get();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static void close(Channel channel, Session session) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(TraceIdUtil.wrap(() -> {
            try {
                // 5秒后若没有主动退出就强制退出
                Thread.sleep(10000);
                channel.disconnect();
                session.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));
    }

    public static void uploadFile(@NonNull SSHInfo sshInfo) {
        log.info("sshInfo:[{}]", sshInfo);
        try {
            Session session = sshInfo.getSession();
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            //创建sftp通信通道
            if (channel == null) {
                throw UnifiedException.gen(" 开启 sftp 通道上传文件到服务器失败 ");
            }
            //指定通道存活时间
            channel.connect(CONNECT_TIMEOUT);
            //开始复制文件
            channel.put(sshInfo.getSourcePath(), sshInfo.getTargetPath());
            close(channel, session);
        } catch (Exception ex) {
            log.error("上传文件失败：", ex);
        }
    }

    public static void downloadFile(@NonNull SSHInfo sshInfo) {
        log.info("sshInfo:[{}]", sshInfo);
        try {
            Session session = sshInfo.getSession();
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            //创建sftp通信通道
            if (channel == null) {
                throw UnifiedException.gen(" 开启 sftp 通道上传文件到服务器失败 ");
            }
            //指定通道存活时间
            channel.connect(CONNECT_TIMEOUT);
            //开始复制文件
            channel.get(sshInfo.getSourcePath(), sshInfo.getTargetPath());
            close(channel, session);
        } catch (Exception ex) {
            log.error("下载文件失败：", ex);
        }
    }

    @Data
    public static class SSHInfo {

        // 账号密码方式
        private String user;        // 服务器账号
        private String password;    // 服务器密码
        private String host;        // 地址
        private Integer port = DEFAULT_PORT;        // 端口号

        // 秘钥方式
        private byte[] privateKey = PRIVATE_KEY;  // 私钥文件
        private byte[] publicKey = PUBLIC_KEY;  // 公钥文件
        private String passphrase;    // 秘钥的密码（如果秘钥进行过加密则需要）

        // 命令执行
        private String cmd;    // 执行命令

        // 上传下载
        private String sourcePath;    // 源文件地址
        private String targetPath;    // 目标地址

        // SSH 端口转发
        private Boolean localForward = false;// 本地转发
        private Boolean remoteForward = false;// 远程转发
        private Integer localPort;// 本地端口
        private String remoteHost = "localhost";// 远程服务器
        private Integer remotePort;// 远程服务端口

        public SSHInfo(String host, String user) {
            this.user = user;
            this.host = host;
        }

        public Session getSession() throws Exception {
            JSch jsch = new JSch();
            //秘钥方式连接
            if (this.getPrivateKey() != null) {
                if (StringUtils.isNotBlank(this.getPassphrase())) {
                    //设置带口令的密钥
                    jsch.addIdentity(null, this.getPrivateKey(), this.getPublicKey(), this.getPassphrase().getBytes());
                } else {
                    //设置不带口令的密钥
                    jsch.addIdentity(null, this.getPrivateKey(), this.getPublicKey(), null);
                }
            }
            //获取session连接
            Session session = jsch.getSession(this.getUser(), this.getHost(), this.getPort());
            //连接失败
            if (session == null) {
                throw UnifiedException.gen("获取 ssh 会话失败");
            }
            //如果密码方式连接  session传入密码
            if (StringUtils.isNotBlank(this.getPassword())) {
                session.setPassword(this.getPassword());
            }
            // 可选配置
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            if (localForward && remoteForward) {
                throw new IllegalArgumentException("不能既本地转发又远程转发");
            }
            // 设置SSH本地端口转发
            if (localForward) {
                int assignedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
                log.info("设置SSH本地端口转发,本地转发到远程, assignedPort:[{}]", assignedPort);
            }
            // 设置SSH远程端口转发
            if (remoteForward) {
                session.setPortForwardingR(remotePort, remoteHost, localPort);
            }

            session.setConfig(config);
            // 在 promptYesNo 方法中return true；就不会在连接的时候询问是否确定要连接
            session.setUserInfo(new UserInfo() {
                @Override
                public String getPassphrase() {
                    return null;
                }

                @Override
                public String getPassword() {
                    return null;
                }

                @Override
                public boolean promptPassword(String s) {
                    return false;
                }

                @Override
                public boolean promptPassphrase(String s) {
                    return false;
                }

                @Override
                public boolean promptYesNo(String s) {
                    return true;
                }

                @Override
                public void showMessage(String s) {
                }
            });
            //设置session通道最大开启时间  默认5分钟  可调用close()方法关闭该通道
            session.connect(SESSION_TIMEOUT);
            return session;
        }

    }


}
