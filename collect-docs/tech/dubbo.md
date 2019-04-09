# 官方地址
[文档地址](http://dubbo.apache.org/zh-cn/docs/user/quick-start.html)

# 工作原理
- service 层，接口层，给服务提供者和消费者来实现的
- config 层，配置层，主要是进行各种配置的
- proxy 层，服务代理层，无论是 consumer 还是 provider，都会生成代理，代理之间进行网络通信
- registry 层，服务注册层，负责服务的注册与发现
- cluster 层，集群层，封装多个服务提供者的路由以及负载均衡，将多个实例组合成一个服务
- monitor 层，监控层，对 rpc 接口的调用次数和调用时间进行监控
- protocol 层，远程调用层，封装 rpc 调用
- exchange 层，信息交换层，封装请求响应模式，同步转异步
- transport 层，网络传输层，抽象 mina 和 netty 为统一接口
- serialize 层，数据序列化层

# spi 思想
```java
@SPI("dubbo")  
public interface Protocol {  
      
    int getDefaultPort();  
  
    @Adaptive  
    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;  
  
    @Adaptive  
    <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;  

    void destroy();  
  
}

// Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

// 在/META_INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol文件中：
// dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
// http=com.alibaba.dubbo.rpc.protocol.http.HttpProtocol
// hessian=com.alibaba.dubbo.rpc.protocol.hessian.HessianProtocol
```
Protocol 接口，在系统运行的时候，，会判断一下应该选用 Protocol 接口的哪个实现类来使用。  
@SPI("dubbo") ，通过 SPI 机制来提供实现类，通过 "dubbo" 作为默认 key 去配置文件里找，配置文件名称与接口全限定名一样的。  
通过 "dubbo" 作为 key 可以找到默认的实现类就是 com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol。  
如果想要动态替换掉默认的实现类，需要使用 @Adaptive 接口，Protocol 接口中，有两个方法加了 @Adaptive 注解，就是说这两个方法会被代理实现。  
在运行的时候会针对 Protocol 生成代理类，这个代理类的这两个方法里面会有代理代码，代理代码会在运行的时候动态根据 url 中的 protocol 来获取那个 key。  
默认是 "dubbo"，也可以自己指定，如果指定了别的 key，那么就会获取别的实现类的实例处理。  

# 通信协议
## dubbo 协议
**默认**就是走 dubbo 协议，单一长连接，进行的是 NIO 异步通信，基于 **hessian** 作为序列化协议。使用的场景是：传输数据量小（每次请求在 100kb 以内），但是并发量很高。  
为了要支持高并发场景，一般是服务提供者就几台机器，但是服务消费者有上百台，可能每天调用量达到上亿次！  
此时用长连接是最合适的，就是跟每个服务消费者维持一个长连接就可以，可能总共就 100 个连接。然后后面直接基于长连接 NIO 异步通信，可以支撑高并发请求。

## rmi 协议
Java 二进制序列化，多个短连接，适合消费者和提供者数量差不多的情况，适用于文件的传输，一般较少用。
   
## hessian 协议
hessian 序列化协议，多个短连接，适用于提供者数量比消费者数量还多的情况，适用于文件的传输，一般较少用。
   
## http 协议
json 序列化，一般较少用。
   
## webservice
SOAP 文本序列化，一般较少用。

# 负载均衡策略
## random load balance
默认情况下使用 random load balance。 **随机**调用实现负载均衡，可以对 provider 不同实例**设置不同的权重**，会按照权重来负载均衡，权重越大分配流量越高，一般就用这个默认的就可以了。

## round robin load balance
均匀地将流量打到各个机器上去，但是如果各个机器的性能不一样，容易导致性能差的机器负载过高。所以此时需要调整权重，让性能差的机器承载权重小一些，流量少一些。

#### least active load balance
如果某个机器性能越差，那么接收的请求越少，越不活跃，此时就会给**不活跃的性能差的机器更少的请求**。

#### consistent hash load balance
一致性 Hash 算法，相同参数的请求一定分发到一个 provider 上去，provider 挂掉的时候，会基于虚拟节点均匀分配剩余的流量，抖动不会太大。  
**如果你需要的不是随机负载均衡**，是要一类请求都到一个节点，那就用一致性 Hash 策略。

# 集群容错策略
## failover cluster 模式
失败自动切换，自动重试其他机器，默认就是这个，常见于读操作。（失败重试其它机器）  

## failfast cluster 模式
一次调用失败就立即失败，常见于写操作。（调用失败就立即失败）  

## failsafe cluster 模式
出现异常时忽略掉，常用于不重要的接口调用，比如记录日志。

## failback cluster 模式
失败了后台自动记录请求，然后定时重发，比较适合于写消息队列这种。  

## forking cluster 模式
**并行调用**多个 provider，只要一个成功就立即返回。  

#### broadcast cluster 模式
逐个调用所有的 provider。  

# 动态代理策略
默认使用 javassist 动态字节码生成，创建代理类。但是可以通过 spi 扩展机制配置自己的动态代理策略。  