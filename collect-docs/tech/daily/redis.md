## redis 部署
```bash
#启动redis
redis-server
#配置启动
redis-server /opt/redis/redis.conf    
# --dir(存放持久化文件和日志文件的目录)   按照参数启动其他配置默认
redis-server --port 6379 --dir /usr/local/data 

#redis基准测试工具
redis-benchmark  
#redis-check-dump aof和rdb持久化文件检测和修复工具
redis-check-aof   
#启动redis-sentinel
redis-sentinel 
```

## 连接客户端
```bash
#命令行客户端
redis-cli
#查看redis的版本信息
redis-cli -v  
#交互式方式连接
redis-cli -h {host} -p {port} 
#命令方式连接
redis-cli -h {host} -p {port} {command}  
#停止redis服务 是否生成持久化文件 optional{nosave|save}  
redis-cli shutdown save
```

## 通用操作
```bash
#所有键  遍历所有键 大量键下禁止使用
keys *  
#键总数 直接获取内置的键总数变量
dbsize  
#键是否存在
exists key 
#删除键
del key...    
#键过期
expire key seconds  
#键的剩余过期时间  -1没设置过过期时间  -2键不存在
ttl key  
#键的数据类型  不存在->none
type key 
#查看内部编码类型
object encoding key 
```

## String
```bash
set key value [ex second] [px millisecond] [nx{键必须不存在}|xx{必须存在}]
setex key seconds value
setnx key value
get key
mset k1 v1 k2 v2
mget k1 k2 k3
```

## Hash
```bash
hset key field value
hsetnx key field value
hget key field
hdel key field ...
hlen key
hmget key f1 f2
hmset key f1 v1 f2 v2
hexists key field
#获取所有的field
hkeys key  
#获取所有的值
hvals key  
#获取所有的field和值
hgetall key 
```

## List(索引下标有序,时间轴,消息队列)
```bash
rpush|lpush key v1 v2
lrange key start end
#在pivot前后插入value
linsert key before|after pivot value 
lindex key index
llen key
#左|右侧弹出元素(删除)
lpop|rpop key  
#删除指定元素  count  >0从左向右删除最多count个value元素  =0删除所有
lrem key count value   
#相当于substring的用法
ltrim key start end  
lset key index newValue
#阻塞命令
blpop|brpop key1 key2 ... timeout 
```

## Set(标签,社交)
```bash
sadd key element1 element2 ...
srem key e1 e2 ...
#计算元素个数
scard key  
#判断元素是否在集合中
sismember key v 
#随机从集合中返回指定个数元素
srandmember key count 
#随机弹出一个元素(删除)
spop key 
#获取所有元素
smembers key 
#多个集合的交集
sinter k1 k2 
#多个集合的并集 
suinon k1 k2 
#多个集合的差集
sdiff k1 k2 
#将k1 k2交集保存到destination中
sinterstore destination k1 k2  
sunionstore destination k1 k2 
sdiffstore destination k1 k2
```

## SortSet(分值有序,排行榜,社交)
```bash
zdd key score member [score member ...] 
zcard key
zscore key member
#计算成员到排名位置
zrank key member  
zrevrank key member
zrem key m1 m2 ..
#给member的score增加value分
zincrby key value member 
#从高到低返回
zrange key start end [withscores] 
zrevrange key start end
#从指定范围返回
zrangebyscore key min max [withscores] [limit offset count] 
zcount key min max
#删除指定排名内到升序元素
zremrangebyrank key start end   
zremrangebyscore key min max
#destination:交集计算结果保存到这个键
#numkeys:需要做交集计算键到个数
#weights:每个键到权重，在做交集计算时，每个键中到每个member 会将自己到分数乘以这个权重值
#aggregate:计算成员交集后，分值按照sum|min|max做汇总，默认sum
zinterstore destination numkeys key .... [weights weight...] [aggregate sum|min|max]
zunionstore destination numkeys key ... [weights weight ...] [aggreate sum|min|max]
```
 