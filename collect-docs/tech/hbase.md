# 概述
hbase 是构建在 hdfs 上的分布式数据库。  
hdfs 的缺点：
- 不支持小文件
- 不支持并发写
- 不支持文件随机修改
- 查询效率也低

## 特征
- 列式存储，和 MySQL 的行式存储不一样。
- 同一个列簇下的列存储在一起，在 Region 的一个 StoreFile 中。
- 按照 Rowkey 进行查找，要查询的字段要想办法放到 Rowkey 中。
- 内部使用 LSM 三层模型进行存储，数据先写到内存的 MemStore 中，内存达到一定阈值再刷写到硬盘 StoreFile 中，再满足一定条件时，小的 StoreFile 会合并为大的 StoreFile。

## 整体架构
Master：均衡 RegionServer 中 Region 的分布存储。当大量 Rowkey 相近的数据都被分配到一个 Region 中，导致这个 Region 数据过大的时候，Region 会进行拆分，HMaster 会对拆分后的 Region 重新分配 RegionServer，这是 HMaster 的负载均衡策略。  
ZooKeeper：保存集群的 meta 元数据，判断数据去哪个 RegionServer。  
RegionServer：存储数据，一个 RegionServer 包括多个 Region。WAL(HLog)先纪录操作日志因为用的是LSM三层存储模型。  
Region：数据按照 rowkey 分配存储在不同的 Region 中。一个 region 有多个 Store。  
Store：LSM三层存储模型，内存(MemStore)->（小文件(StoreFile)->合并成大文件(StoreFile)) 。StoreFile的文件格式是HFile。    

## 写入流程
1. HBase Client 要写输入了，先从 Zookeeper 中拿到 Meta 表信息，根据数据的 Rowkey 找到应该往哪个 RegionServer 写。
2. HBase 会将数据写入对应 RegionServer 的内存 MemStore 中，同时记录操作日志 WAL。
3. 当 MemStore 超过一定阈值，就会将内存 MemStore 中的数据刷写到硬盘上，形成 StoreFile。
4. 在触发了一定条件的时候，小的 StoreFile 会进行合并，变成大的 StoreFile，有利于 HDFS 存储。

## 读取流程
1. HBase Client 要读数据了，先从 Zookeeper 中拿到 Meta 表信息，根据要查的 Rowkey 找到对应的数据在哪些 RegionServer 上。
2. 分别在这些 RegionServer 上根据列簇进行 StoreFile 和 MemStore 的查找，得到很多 key-value 结构的数据。
3. 根据数据的版本(修改删除都是升级版本号，在合并文件的过程中删除低版本的数据)找到最新数据进行返回。

# 列式存储
行式存储方式：

| id | name | age | job |
|:---|:---|:---|:---|
|1  |you |22 |student |
|2  |he  |28 |  |
|3  |she |   |        |

列式存储方式：

| id | key | value |
|:---|:---|:---|
|rowkey:1  |name |you |
|rowkey:1  |age |22 |
|rowkey:1  |job |student |
|rowkey:2  |name |he |
|rowkey:2  |age |28 |
|rowkey:3  |name |she |

rowkey 相同的这些数据其实就是原来的一行。

# 列簇
**获取某个 rowkey 的全部数据如何进行检索？**    
指定列簇(name_age)存储方式：

| id | key | value |
|:---|:---|:---|
|rowkey:1  |name_age:name |you |
|rowkey:1  |name_age:age |22 |
|rowkey:1  |job |student |
|rowkey:2  |name_age:name |he |
|rowkey:2  |name_age:age |28 |
|rowkey:3  |name |she |

默认有一个 job 的列簇，一个 name 列簇，一个 age 列簇。  
现在把 name 和 age 归到 name_age 这一列簇(某个 RegionServer)中。 
其实所有列都是在列簇中，定义表的时候就需要指定列簇。生产环境由于性能考虑和数据均衡考虑，一般只会用一个列簇，最多两个列簇。

# Rowkey
hbase 只能通过 rowkey 来查询。  
有些中间件把 SQL 翻译成 HBase 的查询规则，从而支持了 SQL 查 HBase。  
在 HBase 中，需要把要查询的字段设置在 Rowkey 中，一个 Rowkey 可以理解为一个字符串，而 HBase 就是根据 Rowkey 来建立索引(B+ 树)的。  
查询的时候可能根据年纪查询，也可能根据名字查询一个特定的人。  
Rowkey 有点类似于 MySQL 中的主键，需要保证其唯一性。
Rowkey 就可以这样设计：  

| id | key | value |
|:---|:---|:---|
|rowkey:you,022 |name_age:name |you |
|rowkey:you,022 |name_age:age |22 |
|rowkey:you,022 |job |student |
|rowkey:he,028  |name_age:name |he |
|rowkey:he,028 |name_age:age |28 |
|rowkey:she,000  |name |she |

HBase 提供了三种查询方式：
- 全表扫描。
- 根据一个 Rowkey 进行查询。
- 根据 Rowkey 过滤的范围查询。  

比如你要查 age 不少于 20 的记录，就可以用范围查询，查出从 startRow=0020 到 stopRow=999 的所有记录。  
Rowkey 是按照字符串字典序来组织成 B+ 树的，数字的话需要补齐，不然的话会出现 123 小于 20 的情况，但是补齐的话 020 小于 123。  
如果 Rowkey 复杂且查询条件复杂，HBase 针对 Rowkey 提供了自定义 Filter，所以只要数据在 Rowkey 中有体现能够解析，就能根据自己的条件进行查询。  


