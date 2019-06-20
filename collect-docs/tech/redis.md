# 常见问题

## 不指定 key 失效缓存
> 概述
```
场景描述：key=shopId_goodsId
商品基础信息发生了变更，需要批量更新拥有此商品的所有店铺的商品缓存
```
> 方案
```
goodsVesionKey=goods_version
setGoodsVersionCache(goodsVesionKey,timestamp);
shopGoodsKey=shopId_goodsId_fromCache(goodsVesionKey);
setShopGoodsCache(shopGoodsKey,object);
updateGoodsVersion();
setGoodsVersionCache(goodsVesionKey,timestamp);
shopGoodsKey=shopId_goodsId_fromCache(goodsVesionKey);
此时当获取店铺商品的时候，由于 fromCache(goodsVesionKey) 已变化，所有 shopGoodsKey 的缓存肯定不存在。
核心思路：缓存 key 动态化，使旧 key 过期失效。
```

## 缓存穿透
> 概述
``` 
缓存穿透：查询不存在数据的现象
请求去查询数据库中根本就不存在的数据，也就是缓存和数据库都查询不到这条数据，但是请求每次都会打到数据库上面去。
如果有人你的系统进行攻击，拿一个不存在的 id 去查询数据，会产生大量的请求到数据库去查询。可能会导致数据库由于压力过大而宕掉。
```
> 方案一 缓存空值
```
key 对应的值设置为 null(特定的字符串) 放到缓存里面去。这样就不会请求到数据库，但是别忘了设置过期时间。
对于空数据的 key 有限的，可以采用这种方式进行缓存。
```
> 方案二 布隆过滤 BloomFilter
```
BloomFilter 用来判断某个元素（key）是否存在于某个集合中。
BloomFilter 可以加在 缓存空值 之前，在缓存之前在加一层 BloomFilter ，查询的时候先去 BloomFilter 去查询 key 是否存在，如果不存在就直接返回，存在再走查缓存，查DB。
针对于一些恶意攻击，攻击带过来的大量 key 是不存在的，采用 缓存空值 就会缓存大量不存在key的数据。
针对这种 key 异常多、请求重复率比较低的数据，就没有必要进行缓存，使用第二种方案直接过滤掉。
```
## 缓存击穿
> 概述
``` 
缓存击穿：在平常高并发的系统中，大量的请求同时查询一个 key 时，此时这个 key 正好失效了，就会导致大量的请求都打到数据库上面去的现象。
可能会导致数据库由于压力过大而宕掉。
```
> 方案
```
1. 失效时间错开
为了避免这些热点的数据集中失效，在设置缓存过期时间的时候，需要让他们失效的时间错开。比如在一个基础的时间上加上或者减去一个范围内的随机值。
2. 互斥锁
多个线程同时去查询数据库的这条数据，可以在第一个查询数据的请求上加互斥锁。其他的线程走到这一步就阻塞，等第一个线程查询到了数据，然后做缓存。后面的线程进来发现已经有缓存了，就直接走缓存。
但是也是由于它会阻塞其他的线程，此时系统吞吐量会下降。需要结合实际的业务去考虑是否使用
public Object getObject(){
    if(isCache) return cache;
    synchronized (lock){
        if(isCache) return cache;
        cache object from db
        return object
    }
}
```
## 缓存雪崩
> 概述
``` 
缓存雪崩：当某一时刻发生大规模的缓存失效的情况，比如缓存服务宕机了，会有大量的请求进来直接打到DB上面。结果就是 数据库 宕机。
```
> 方案
```
缓存使用的核心就是要避免 数据库 宕机，所以需要想其他方案来避免此事发生。
未雨绸缪：
1. 使用集群缓存，保证缓存服务的高可用
2. ehcache本地缓存(多级缓存)，本地缓存可能会有分布式不一致的情况,内存的问题
3. Hystrix限流&降级
事故处理：
1. 尽快恢复缓存集群，预热数据
```

# Redis

## 注意点
- 避免一次性遍历集合类型的所有成员，而应使用 SCAN 类的命令进行分批的，游标式的遍历。  
- 不要使用过长的 Key，会消耗更多的内存，还会导致查找的效率降低。
- 使用高耗时的 Redis 命令是很危险的，会占用唯一的一个线程的大量处理时间，导致所有的请求都被拖慢

## String
相关的常用命令：
- SET：为一个 Key 设置 Value，可以配合 EX/PX 参数指定 Key 的有效期，通过 NX/XX 参数针对 Key 是否存在的情况进行区别操作，时间复杂度 O(1)。
- GET：获取某个 Key 对应的 Value，时间复杂度 O(1)。
- GETSET：为一个 Key 设置 Value，并返回该 Key 的原 Value，时间复杂度 O(1)。
- MSET：为多个 Key 设置 Value，时间复杂度 O(N)。
- MSETNX：同 MSET，如果指定的 Key 中有任意一个已存在，则不进行任何操作，时间复杂度O(N)。
- MGET：获取多个 Key 对应的 Value，时间复杂度O(N)。
- INCR：将 Key 对应的 Value 值自增 1，并返回自增后的值。只对可以转换为整型的 String 数据起作用。时间复杂度 O(1)。
- INCRBY：将 Key 对应的 Value 值自增指定的整型数值，并返回自增后的值。只对可以转换为整型的 String 数据起作用。时间复杂度 O(1)。
- DECR/DECRBY：同 INCR/INCRBY，自增改为自减。

## List
- LPUSH：向指定 List 的左侧（即头部）插入 1 个或多个元素，返回插入后的 List 长度。时间复杂度O(N)，N 为插入元素的数量。
- RPUSH：同 LPUSH，向指定 List 的右侧（即尾部）插入 1 或多个元素。
- LPOP：从指定 List 的左侧（即头部）移除一个元素并返回，时间复杂度 O(1)。
- RPOP：同 LPOP，从指定 List 的右侧（即尾部）移除 1 个元素并返回。
- LPUSHX/RPUSHX：与 LPUSH/RPUSH 类似，区别在于 LPUSHX/RPUSHX 操作的 Key 如果不存在，则不会进行任何操作。
- LLEN：返回指定 List 的长度，时间复杂度 O(1)。
- LRANGE：返回指定 List 中指定范围的元素（双端包含，即 LRANGE key 0 10 会返回 11 个元素），时间复杂度 O(N)。  

ps:
Redis 的 List 实际是设计来用于实现队列，而不是用于实现类似 ArrayList 这样的列表的。  
应尽可能控制一次获取的元素数量，一次获取过大范围的 List 元素会导致延迟，同时对长度不可预知的 List，避免使用 LRANGE key 0 -1 这样的完整遍历操作。  
Redis 还提供了一系列阻塞式的操作命令，如 BLPOP/BRPOP 等，能够实现类似于 BlockingQueue 的能力，即在 List 为空时，阻塞该连接，直到 List 中有对象可以出队时再返回。  

## Hash
- HSET：将 Key 对应的 Hash 中的 field 设置为 Value。如果该 Hash 不存在，会自动创建一个。时间复杂度 O(1)。
- HGET：返回指定 Hash 中 field 字段的值，时间复杂度 O(1)。
- HMSET/HMGET：同 HSET 和 HGET，可以批量操作同一个 Key 下的多个 field，时间复杂度：O(N)，N 为一次操作的 field 数量。
- HSETNX：同 HSET，但如 field 已经存在，HSETNX 不会进行任何操作，时间复杂度 O(1)。
- HEXISTS：判断指定 Hash 中 field 是否存在，存在返回 1，不存在返回 0，时间复杂度 O(1)。
- HDEL：删除指定 Hash 中的 field（1 个或多个），时间复杂度：O(N)，N 为操作的 field 数量。
- HINCRBY：同 INCRBY 命令，对指定 Hash 中的一个 field 进行 INCRBY，时间复杂度 O(1)。

## Set
- SADD：向指定 Set 中添加 1 个或多个 Member，如果指定 Set 不存在，会自动创建一个。时间复杂度 O(N)，N 为添加的 Member 个数。
- SREM：从指定 Set 中移除 1 个或多个 Member，时间复杂度 O(N)，N 为移除的 Member 个数。
- SRANDMEMBER：从指定 Set 中随机返回 1 个或多个 Member，时间复杂度 O(N)，N 为返回的 Member 个数。
- SPOP：从指定 Set 中随机移除并返回 Count 个 Member，时间复杂度O(N)，N 为移除的 Member 个数。
- SCARD：返回指定 Set 中的 Member 个数，时间复杂度 O(1)。
- SISMEMBER：判断指定的 Value 是否存在于指定 Set 中，时间复杂度 O(1)。
- SMOVE：将指定 Member 从一个 Set 移至另一个 Set。

## Sorted Set
- ZADD：向指定 Sorted Set 中添加 1 个或多个 Member，时间复杂度 O(Mlog(N))，M 为添加的 Member 数量，N 为 Sorted Set 中的 Member 数量。
- ZREM：从指定 Sorted Set 中删除 1 个或多个 Member，时间复杂度 O(Mlog(N))，M 为删除的 Member 数量，N 为 Sorted Set 中的 Member 数量。
- ZCOUNT：返回指定 Sorted Set 中指定 Score 范围内的 Member 数量，时间复杂度：O(log(N))。
- ZCARD：返回指定 Sorted Set 中的 Member 数量，时间复杂度 O(1)。
- ZSCORE：返回指定 Sorted Set 中指定 Member 的 Score，时间复杂度 O(1)。
- ZRANK/ZREVRANK：返回指定 Member 在 Sorted Set 中的排名，ZRANK 返回按升序排序的排名，ZREVRANK 则返回按降序排序的排名。时间复杂度 O(log(N))。
- ZINCRBY：同 INCRBY，对指定 Sorted Set 中的指定 Member 的 Score 进行自增，时间复杂度 O(log(N))。

## Bitmap 和 HyperLogLog
Bitmap 在 Redis 中不是一种实际的数据类型，而是一种将 String 作为 Bitmap 使用的方法。  
可以理解为将 String 转换为 bit 数组。使用 Bitmap 来存储 true/false 类型的简单数据极为节省空间。  
HyperLogLogs 是一种主要用于数量统计的数据结构，它和 Set 类似，维护一个不可重复的 String 集合，但是 HyperLogLogs 并不维护具体的 Member 内容，只维护 Member 的个数。  
也就是说，HyperLogLogs **只能用于计算一个集合中不重复的元素数量**，所以它比 Set 要节省很多内存空间。  
 
## 其他常用命令：
- EXISTS：判断指定的 Key 是否存在，返回 1 代表存在，0 代表不存在，时间复杂度 O(1)。
- DEL：删除指定的 Key 及其对应的 Value，时间复杂度 O(N)，N 为删除的 Key 数量。
- EXPIRE/PEXPIRE：为一个 Key 设置有效期，单位为秒或毫秒，时间复杂度 O(1)。
- TTL/PTTL：返回一个 Key 剩余的有效时间，单位为秒或毫秒，时间复杂度 O(1)。
- RENAME/RENAMENX：将 Key 重命名为 Newkey。使用 RENAME 时，如果 Newkey 已经存在，其值会被覆盖。使用 RENAMENX 时，如果 Newkey 已经存在，则不会进行任何操作，时间复杂度 O(1)。
- TYPE：返回指定 Key 的类型，String，List，Set，Zset，Hash。时间复杂度 O(1)。
- CONFIG GET：获得 Redis 某配置项的当前值，可以使用 * 通配符，时间复杂度 O(1)。
- CONFIG SET：为 Redis 某个配置项设置新值，时间复杂度 O(1)。
- CONFIG REWRITE：让 Redis 重新加载 redis.conf 中的配置。
- slowlog-log-slower-than xxxms  执行时间慢于xxx毫秒的命令计入Slow Log
- slowlog-max-len xxx  Slow Log的长度，即最大纪录多少条Slow Log

## 数据持久化
Redis 提供了将数据定期自动持久化至硬盘的能力，包括 RDB 和 AOF 两种方案，两种方案分别有其长处和短板，可以配合起来同时运行，确保数据的稳定性。  
如果你只把 Redis 作为缓存服务使用，Redis 中存储的所有数据都不是该数据的主体而仅仅是同步过来的备份，那么可以关闭 Redis 的数据持久化机制。  
但通常来说，仍然建议至少开启 RDB 方式的数据持久化。  
RDB 方式的持久化几乎不损耗 Redis 本身的性能，在进行 RDB 持久化时，Redis 主进程唯一需要做的事情就是 Fork 出一个子进程，所有持久化工作都由子进程完成。  
Redis 无论因为什么原因 Crash 掉之后，重启时能够自动恢复到上一次 RDB 快照中记录的数据。
这省去了手工从其他数据源（如 DB）同步数据的过程，而且要比其他任何的数据恢复方式都要快。

### RDB
恢复数据时比使用 AOF 要快很多。快照是定期生成的，所以在 Redis Crash 时或多或少会丢失一部分数据。  
如果数据集非常大且 CPU 不够强（比如单核 CPU），Redis 在 Fork 子进程(拷贝物理地址页)时可能会消耗相对较长的时间（长至 1 秒），影响这期间的客户端请求。
```
save [seconds] [changes]
在 [seconds] 秒内如果发生了 [changes] 次数据修改，则进行一次 RDB 快照保存。  
可以配置多条 Save 指令，让 Redis 执行多级的快照保存策略。
```

### AOF
AOF 默认是关闭的，如要开启，进行如下配置： appendonly yes  
AOF 提供了三种 Fsync(刷盘) 配置，always/everysec/no，通过配置项 appendfsync 指定：  
- appendfsync no：不进行 Fsync，将 Flush 文件的时机交给 OS 决定，速度最快。  
- appendfsync always：每写入一条日志就进行一次 Fsync 操作，数据安全性最高，但速度最慢。  
- appendfsync everysec：折中的做法，交由后台线程每秒 Fsync 一次。  

在启用 appendfsync always 时，任何已写入的数据都不会丢失，使用在启用 appendfsync everysec 也至多只会丢失 1 秒的数据。   
AOF 文件易读，可修改，在进行了某些错误的数据清除操作后，只要 AOF 文件没有 Rewrite，就可以把 AOF 文件备份出来，把错误的命令删除，然后恢复数据。  

#### 日志重写
采用 AOF 持久方式时，Redis 会把每一个写请求都记录在一个日志文件里。
大量的无用日志会让 AOF 文件过大，也会让数据恢复的时间过长。  
Redis 提供了 AOF Rewrite 功能，可以重写 AOF 文件，只保留能够把数据恢复到最新状态的最小写操作集。  
```
auto-aof-rewrite-percentage 100  
auto-aof-rewrite-min-size 64mb  
Redis 在每次 AOF Rewrite 时，会记录完成 Rewrite 后的 AOF 日志大小，当 AOF 日志大小在该基础上增长了 100% 后，自动进行 AOF Rewrite。  
如果增长的大小没有达到 64MB，则不会进行 Rewrite。  
```

## 内存管理
默认情况下，在 32 位 OS 中，Redis 最大使用 3GB 的内存，在 64 位 OS 中则没有限制。  
在使用 Redis 时，应该对数据占用的最大空间有一个基本准确的预估，并为 Redis 设定最大使用的内存。  
否则在 64 位 OS 中 Redis 会无限制地占用内存（当物理内存被占满后会使用 Swap 空间），容易引发各种各样的问题。  
通过如下配置控制 Redis 使用的最大内存： maxmemory 100mb  
在内存占用达到了 maxmemory 后，再向 Redis 写入数据时，Redis 会根据配置的数据淘汰策略尝试淘汰数据，释放空间。  
如果没有数据可以淘汰或者没有配置数据淘汰策略，那么 Redis 会对所有写请求返回错误，但读请求仍然可以正常执行。  
```
如果采用了 Redis 的主从同步，主节点向从节点同步数据时，会占用掉一部分内存空间。  
如果 maxmemory 过于接近主机的可用内存，导致数据同步时内存不足。  
设置的 maxmemory 不要过于接近主机可用的内存，留出一部分预留用作主从同步。  
```

## 数据淘汰机制
惰性删除(获取时判断是否过期)。  
定时删除，Redis 提供了 5 种数据淘汰策略：
- volatile-lru：使用 LRU 算法进行数据淘汰（淘汰上次使用时间最早的，且使用次数最少的 Key），只淘汰设定了有效期的 Key。
- allkeys-lru：使用 LRU 算法进行数据淘汰，所有的 Key 都可以被淘汰。
- volatile-random：随机淘汰数据，只淘汰设定了有效期的 Key。
- allkeys-random：随机淘汰数据，所有的 Key 都可以被淘汰。
- volatile-ttl：淘汰剩余有效期最短的 Key。  

最好为 Redis 指定一种有效的数据淘汰策略以配合 maxmemory 设置，避免在内存使用满后发生写入失败的情况。
一般来说，推荐使用的策略是 volatile-lru，并辨识 Redis 中保存的数据的重要性。

## Pipelining
减少维护网络连接和传输数据所消耗的资源和时间。  
Redis 提供的 Pipelining 功能来实现在一次交互中执行多条命令。  
使用 Pipelining 时，只需要从客户端一次向 Redis 发送多条命令（以 rn）分隔，Redis 就会依次执行这些命令，并且把每个命令的返回按顺序组装在一起一次返回。    

### Scripting
通过 EVAL 与 EVALSHA 命令，可以让 Redis 执行 LUA 脚本。  
这就类似于 RDBMS 的存储过程一样，可以把客户端与 Redis 之间密集的读/写交互放在服务端进行，避免过多的数据交互，提升性能。  
Scripting 功能是作为**事务功能**的替代者诞生的，事务提供的所有能力 Scripting 都可以做到。Redis 官方推荐使用 LUA Script 来代替事务，前者的效率和便利性都超过了事务。  

## 主从复制 哨兵部署
Redis 支持一主多从的主从复制架构(通过持久化文件进行复制)。一个 Master 实例负责处理所有的写请求，Master 将写操作同步至所有 Slave。  
借助 Redis 的主从复制，可以实现读写分离和高可用：  
- 实时性要求不是特别高的读请求，可以在 Slave 上完成，提升效率。特别是一些周期性执行的统计任务，这些任务可能需要执行一些长耗时的 Redis 命令，可以专门规划出 1 个或几个 Slave 用于服务这些统计任务。
- 借助 Redis Sentinel 可以实现高可用，当 Master Crash 后，Redis Sentinel 能够自动将一个 Slave 晋升为 Master，继续提供服务。

## 集群分片 集群部署
各实例之间同步元数据使用的是 Gossip 协议，保证集群实例的无限扩展。  
Redis Cluster 的每个数据分片都采用了主从复制的结构。唯一的区别是省去了 Redis Sentinel 这一额外的组件，由 Redis Cluster 负责进行一个分片内部的节点监控和自动 Failover。  
出现以下场景时就需要集群部署：  
- Redis 的写请求并发量大，一个 Redis 实例以无法承载。
- Redis 中存储的数据量大，一台主机的物理内存已经无法容纳。

### 分片原理
Redis Cluster 中共有 16384 个 hash slot，Redis 会计算每个 Key 的 CRC16，将结果与 16384 取模，来决定该 Key 存储在哪一个 hash slot 中。  
同时需要指定 Redis Cluster 中每个数据分片负责的 Slot 数。Slot 的分配在任何时间点都可以进行重新分配。  
客户端在对 Key 进行读写操作时，可以连接 Cluster 中的任意一个分片，如果操作的 Key 不在此分片负责的 Slot 范围内，Redis Cluster 会自动将请求重定向到正确的分片上。  

### Hash Tags routing 功能
以 hash tags 要求的格式的 Key，将会确保进入同一个 Slot 中。  
例如：{uiv}user:1000 和 {uiv}user:1001 拥有同样的 hash tag {uiv}，会保存在同一个 Slot 中。  
使用 Redis Cluster 时，Pipelining、事务和 LUA Script 功能涉及的 Key 必须在同一个数据分片上，否则将会返回错误。  

