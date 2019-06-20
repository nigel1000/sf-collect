
# TCP 特点
- 服务器会置于Listen（等待响应）状态，等待客户端连接。
- 单工，采取一问一答的模式，必须由客户端发起。
- 滑动窗口，表示发送端和接收端的接收能力
- 拥塞窗口，表示中间设备的传输能力

# 握手和挥手状态变更
SYN：synchronization  
seq：sequence  
ACK：acknowledgement  
FIN：finish  
小写的ack是确认编号  
大写的ACK是表示确认报文  
大写的SYN是表示同步报文  
## 三次握手
客户端向服务器发送报文（你好），发出请求SYN=1，同时选择一个初始序号seq=x。**客户端的状态更改为SYN-SENT**
服务器收到报文，**服务器的状态从LISTEN变为SYNC_RCVD**  
服务器向客户端发出报文（收到，你好），SYN=1,ACK=1,ack=x+1，同时发送序号为seq=y。
客户端收到报文，**客户端的状态更改为ESTABLISHED(建立连接)**
客户端向服务器发送报文（收到）。返回服务器ACK=1，ack=y+1，seq=x+1。
服务器接收到报文，**服务器的状态更改为ESTABLISHED(建立连接)**
此时可以畅通无阻地交流，因为是单工，需要客户端主动发起，客户端发送报文（我的请求是我要订单的数据），。。。
## 四次挥手
客户端发出连接断连报文（断连），并且停止发送数据。设置报文FIN=1，其序列号为seq=u，**客户端的状态是FIN-WAIT-1（终止待待1）**  
服务器收到连接释放报文发出确认报文（收到），ACK=1，ack=u+1，seq=v。**服务器的状态更改为CLOSE_WAIT状态**  
客户端收到报文，**客户端的状态更改为FIN-WAIT-2**
服务器**将最后的请求处理完毕后**，就向客户端发送连接释放报文（断连）FIN=1，ACK=1, ack=u+1，序列号为seq=w。**服务器的状态更改为LAST_ACK**  
客户端收到报文，**客户端的状态更改为TIME_WAIT**  
客户端必须发出确认报文，ACK=1，ack=w+1，seq=u+1。
服务端收到报文，**服务器的状态更改为CLOSED**  
客户端在等待2MSL(两次交谈响应时间)后，**客户端的状态更改为CLOSED**  
## 总结
- 每一次通讯，都带有seq序列码。 每一次通讯，状态都会变更。  
- SYN=1在握手阶段双方各发送一次。FIN=1在挥手阶段双方各发送一次。  
- 有确认码的时候ACK=1，必带有确认号ack。 ack在上一条接收到的序号上+1。  

# 滑动窗口
TCP头里有一个字段叫Window，又叫Advertised-Window，这个字段是接收端告诉发送端自己还有多少缓冲区可以接收数据。  
发送端就可以根据这个接收端的处理能力来发送数据，而不会导致接收端处理不过来。  
通过Sliding Window来做流控，避免网络拥塞导致丢包。     

# 拥塞处理
tcp 通过一个timer采样了RTT并计算RTO，但是如果网络上的延时突然增加，TCP对这个事做出的应对只有重传数据。  
重传会导致网络的负担更重，于是会导致更大的延迟以及更多的丢包，这个情况就会进入恶性循环被不断地放大。  
如果一个网络内有成千上万的TCP连接都这么行事，那么马上就会形成“网络风暴”，TCP这个协议就会拖垮整个网络。  
TCP不是一个自私的协议，当拥塞发生的时候，要做自我牺牲。就像交通阻塞一样，每个车都应该把路让出来，而不要再去抢路了。  
拥塞控制主要是四个算法：
- 慢启动
- 拥塞避免
- 拥塞发生
- 快速恢复

## 慢启动
慢启动的算法如下：(cwnd全称Congestion Window)
1）连接建好的开始先初始化cwnd = 1，表明可以传一个MSS大小的数据。  
2）每当收到一个ACK，cwnd++; 呈线性上升  
3）每当过了一个RTT，cwnd = cwnd*2; 呈指数让升  
4）还有一个ssthresh（slow start threshold），是一个上限，当cwnd >= ssthresh时，就会进入“拥塞避免算法”  

## 拥塞避免
一般来说ssthresh的值是65535字节。  
拥塞避免算法 – Congestion Avoidance 如下：  
- 收到一个ACK时，cwnd = cwnd + 1/cwnd
- 当每过一个RTT时，cwnd = cwnd + 1
这样就可以避免增长过快导致网络拥塞，慢慢的增加调整到网络的最佳值。很明显，是一个线性上升的算法。  

## 拥塞发生
当丢包的时候，会有两种情况：
1）等到RTO超时，重传数据包。TCP认为这种情况太糟糕，反应也很强烈。  
- sshthresh =  cwnd /2
- cwnd 重置为 1
- 进入慢启动过程
2）Fast Retransmit算法，也就是在收到3个duplicate ACK时就开启重传，而不用等到RTO超时。
- TCP Tahoe的实现和RTO超时一样。
- TCP Reno的实现是：
    - sshthresh = cwnd
    - cwnd = cwnd /2
    - 进入快速恢复算法——Fast Recovery

RTO超时后，sshthresh会变成cwnd的一半。 
如果cwnd<=sshthresh时出现的丢包，TCP的sshthresh就会减了一半，等cwnd又很快地以指数级增涨爬到这个地方时，就会成慢慢的线性增涨。  

## 快速恢复算法  
TCP Reno算法如下:
- cwnd = sshthresh  + 3 * MSS （3的意思是确认有3个数据包被收到）
- 重传Duplicated ACKs指定的数据包
- 如果再收到 duplicated Acks，那么cwnd = cwnd +1
- 如果收到了新的Ack，cwnd = sshthresh ，然后就进入了拥塞避免的算法。
它依赖于3个重复的Acks。3个重复的Acks并不代表只丢了一个数据包，很有可能是丢了好多包。  
这个算法只会重传一个，而剩下的那些包只能等到RTO超时，于是进入了恶梦模式，超时一个窗口就减半一下，多个超时会超成TCP的传输速度呈级数下降，而且也不会触发Fast Recovery算法了。  

TCP New Reno算法如下:
- 当sender收到了3个Duplicated Acks，进入Fast Retransimit模式，重传重复Acks指示的那个包。如果只有这一个包丢了，重传这个包后回来的Ack会把整个已经被sender传输出去的数据ack回来。如果没有的话，说明有多个包丢了。叫这个ACK为Partial ACK。  
- 一旦Sender这边发现了Partial ACK出现，sender就可以推理出来有多个包被丢，于是继续重传sliding window里未被ack的第一个包。直到再也收不到Partial Ack，才真正结束Fast Recovery这个过程  


