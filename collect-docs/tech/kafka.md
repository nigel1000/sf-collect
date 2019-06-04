# 为什么需要消息队列
A，B系统都需要随着C系统的更新事件进行后续操作处理。  
## 解耦
没有消息队列，D系统也需要随着C系统的更新事件进行后续操作，C系统需要迭代后配合上线。  
## 异步
没有消息队列，C系统需要循环调用A，B系统的同步接口进行处理。此时C系统的更新事件性能会急速下降。 
## 削峰
当C系统的更新事件发现频率超出了A，B系统同步接口的承载能力时，此时A，B系统会宕机。  

# 基础架构
## Producer
Producer 即生产者，消息的产生者，是消息的入口。  
## Broker
Broker 是 Kafka 实例，每个服务器上有一个或多个 Kafka 的实例。  
每个 Kafka 集群内的 Broker 都有一个不重复的编号，例如 Broker-0、Broker-1   
## Topic
消息的主题，，Kafka 的数据就保存在 Topic。在每个 Broker 上都可以创建多个 Topic。  
## Partition
Topic 的分区，每个 Topic 可以有多个分区，分区的作用是提高并发和方便扩展，提高 Kafka 的吞吐量。  
同一个 Topic 在不同的分区的数据是不重复的，Partition 的表现形式就是一个一个的文件夹。  
## Replication
每一个分区都有多个副本。当主分区（Leader）故障的时候会选择一个 Follower 成为 Leader。
在 Kafka 中默认副本的最大数量是 10 个，且副本的数量不能大于 Broker 的数量，Follower 和 Leader 绝对是在不同的机器，同一机器对同一个分区也只可能存放一个副本（包括自己）。  
## Message
每一条发送的消息主体。  
消息主要包含消息体、消息大小、Offset、压缩类型等等。  
- Offset：Offset 是一个占 8byte 的有序 id 号，它可以唯一确定每条消息在 Partition 内的位置！
- 消息大小：消息大小占用 4byte，用于描述消息的大小。
- 消息体：消息体存放的是实际的消息数据（被压缩过），占用的空间根据具体的消息而不一样。
## Consumer
消费者，即消息的消费方，是消息的出口。
## Consumer Group
可以将多个消费组组成一个消费者组，同一个分区的数据只能被消费者组中的某一个消费者消费。
同一个消费者组的消费者可以消费同一个 Topic 的不同分区的数据。  
## Zookeeper
Kafka 集群依赖 Zookeeper 来保存集群的的元信息，来保证系统的可用性。 

# 发送数据
1. 发送者先从集群获取topic分区leader
2. 发送消息给leader
3. leader将消息写入本地文件
4. follower从leader pull消息
5. follower将消息写入本地后向leader发送 ack
6. leader收到所有ack后向发送者发送ack
## 如何确定写到那个分区 Partition
- 在写入的时候可以指定需要写入的 Partition，如果有指定，则写入对应的 Partition。
- 如果没有指定 Partition，但是设置了数据的 Key，则会根据 Key 的值 Hash 出一个 Partition。
- 如果既没指定 Partition，又没有设置 Key，则会轮询选出一个 Partition。
## 何时向发送者发送 ack
配置参数可设置的值为 0、1、all：  
- 0 代表 Producer 往集群发送数据不需要等到集群的返回，不确保消息发送成功。安全性最低但是效率最高。
- 1 代表 Producer 往集群发送数据只要 Leader 应答就可以发送下一条，只确保 Leader 发送成功。
- all 代表 Producer 往集群发送数据需要所有的 Follower 都完成从 Leader 的同步才会发送下一条，确保 Leader 发送成功和所有的副本都完成备份。安全性最高，但是效率最低。
## 如何保存数据
Partition 在服务器上的表现形式就是一个一个的文件夹，每个 Partition 的文件夹下面会有多组 Segment 文件。  
每组 Segment 文件又包含 .index 文件、.log 文件、.timeindex 文件三个文件。  
log 文件就是实际存储 Message 的地方，而 index 和 timeindex 文件为索引文件，用于检索消息。  
文件的命名是以该 Segment 最小 Offset 来命名的。  
假如有000.index，156.index两个文件，则 000.index 存储 Offset 为 0~155 的消息。  
## 数据删除策略
- 基于时间，默认配置是 168 小时（7 天）。
- 基于大小，默认配置是 1073741824。  
Kafka 读取特定消息的时间复杂度是 O(1)，删除过期的文件并不会提高 Kafka 的性能。 

# 消费数据
消息存储在 Log 文件后，消费者就可以进行消费了。  
消费者主动的去 Kafka 集群拉取消息，消费者在拉取消息的时候也是先找 Leader 去拉取。  
多个消费者可以组成一个消费者组（Consumer Group），每个消费者组都有一个组 id。  
同一个消费组者的消费者可以消费同一 Topic 下不同分区的数据，但是不会组内多个消费者消费同一分区的数据。
当消费者比分区多时，就会出现有消费者闲置不消费的情况。  
当分区比消费者多时，就会出现有消费者消费多个分区的情况。  
## 如何根据 Offset 找到需要消费的数据
Offset = 159
1. 先找到 Offset 的 message 所在的 Segment 文件（利用二分法查找）。  
2. 打开找到的 Segment 中的 .index 文件（也就是 156.index 文件，该文件起始偏移量为 156+1。
要查找的 Offset 为 159 的 Message 在该 Index 内的偏移量为 156+3=159。所以这里要查找的相对 Offset 为 3）。  
由于Index文件采用的是稀疏索引的方式存储着相对 Offset 及对应 Message 物理偏移量的关系，所以直接找相对 Offset 为 3 的索引找不到。  
利用二分法查找相对 Offset 小于或者等于指定的相对 Offset 的索引条目中最大的那个相对 Offset。  
index 文件存储着 相对 Offset 和 log 文件存储Message的物理偏移位置。  
3. 根据找到的相对 Offset 的索引确定 log 文件 Message 存储的物理偏移位置。
从物理偏移位置开始顺序扫描直到找到 Offset 为 159 的那条 Message。   
## 消费者消费的 Offset 存储在哪？
在早期的版本中，消费者将消费到的 Offset 维护在 Zookeeper 中，Consumer 每间隔一段时间上报一次，这里容易导致重复消费，且性能不好。  
在新的版本中消费者消费到的 Offset 已经直接维护在 Kafka 集群的 __consumer_offsets 这个 Topic 中。  

