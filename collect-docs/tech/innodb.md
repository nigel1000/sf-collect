## Explain Type

最为常见的扫描方式且性能由高到低如下：

- system：系统表，少量数据，往往不需要进行磁盘IO；
- const：常量连接；
- eq_ref：主键索引或者非空唯一索引等值扫描；
- ref：非主键非唯一索引等值扫描；
- range：范围扫描；
- index：索引树扫描；
- ALL：全表扫描；

### system

数据已经加载到内存里，不需要进行磁盘IO   

-  从系统表 time_zone 查询数据  


```mysql
explain select * from mysql.time_zone;
```

- 外层嵌套从临时表查询  

```mysql
explain select * from (select * from user where id=1) tmp;
```

### const

主键或者唯一索引上的等值查询  

```mysql
# 查询条件value是1, 并且 ID 为
# 主键|非空唯一索引 时为 const
# 普通索引 时为 ref
explain select * from user where id=1;
```

### eq_ref&ref

eq_ref：主键或者唯一索引上的join查询，对于前表的每一行，后表只有一行被扫描。   

ref：普通索引的join查询，对于前表的每一行，后表可能有多于一行的数据被扫描。   

```mysql
# ID 为
# 主键|非空唯一索引 时为 eq_ref
# 普通索引 时为 ref
# 无索引 时为 all
explain select * from user,user_ex where user.id=user_ex.id;
```

### range

索引上的范围查询  

```mysql
# id 必须是索引
explain select * from user where id between 1 and 4;
explain select * from user where id in(1,2,3);
explain select * from user where id>3;
```

### index

走索引但需扫描索引上的全部数据  

```mysql
# id是主键，count 查询需要通过扫描索引上的全部数据来计数。
explain select count (*) from user;
```

### all

对于前表的每一行，后表都要被全表扫描   

```mysql
explain select * from user,user_ex where user.id=user_ex.id;
```

## Innodb 索引数据结构选择

[数据结构](数据结构.md)  

局部性原理：当一个数据被用到时，其附近的数据也通常会马上被使用。   

操作系统从磁盘读取数据到内存是以磁盘块（Block）为基本单位的，位于同一个磁盘块中的数据会被一次性读取出来。  

- B Tree 中间节点也存有数据，故 B+Tree 内存可以存储的节点数量大于 B Tree，这在查询的时候，B Tree 发生磁盘IO次数是大于 B+Tree 的。  
-  B+Tree 叶子节点之间有指针连接，在范围查询是可以通过链表遍历而无需从上一级父节点查。  

鉴于此，一般关系型数据库中都选择  B+Tree 来存储数据。 

### InnoDB 引擎 若干特点

- InnoDB 存储引擎有页的概念，默认每个页的大小为 16K，每次读取数据时都是读取 4*4K 的大小。  
- 插入数据时，一般是插入到当前行的后面或者是已删除行留下来的空间。并持有指向下一条记录的指针。
-  page1 空间使用完完后会分裂产生两个节点page2和page3，把page1的数据拷贝到page2，新数据插入到page3。原来的page1作为根节点只存放索引key(数据的主键)。
- InnoDB 中根节点是会预读到内存中的，所以结点的物理地址固定会比较好。所以进行分裂的时候选择让page1变成根节点，而不是新建page2为根节点(以page1的数据拷贝到page2为代价)，page1和page3成为page2的叶子节点。
- page1 空间只能存储 10 条数据，在插入第 11 条数据的时候会进行裂变，根据B+Tree 特性，这是一棵 11 阶的树，裂变之后每个结点的元素至少为 11/2=5 个。主键 1-5 的数据还是在原来的页，主键 6-11 的数据会放到新的页，根结点存放主键 6。如果是这样的话，页空间利用率只有 50%，并且会导致更为频繁的页分裂。InnoDB 对聚簇索引做了优化，新的数据放入新创建的页，不移动原有页面的任何记录。  

### InnoDB 引擎 数据查找

- 根据数据的主键在根page中查到相应叶子节点page加载到内存   
- 在page中查找具体的数据   

### 聚集索引

主键聚簇索引叶子节点存有聚簇索引字段和具体数据    

![image-20190706074301517](../images/InnodbPage.png)

主键自增写入时新插入的数据不会影响到原有页，插入效率高，且页的利用率高。    

但是如果主键是无序的或者随机的，每次的插入可能会导致原有页频繁的分裂，影响插入效率，降低页的利用率。  

如果表**频繁的插入和删除会导致数据页产生碎片**，页的空间利用率低，还会导致树变的“虚高”，降低查询效率，可以通过**索引重建**来消除碎片提高查询效率。  

### 非聚集索引

非聚簇索引的叶子节点只存有非聚簇索引字段和聚簇索引字段.      

![image-20190706075743229](../images/Innodb非聚集索引.png)

索引根据非聚簇索引进行查询是会有**二次查询**和**覆盖索引**的概念   

> 使用 MyISAM 引擎时则没有二次查询的问题，索引树的叶子结点的数据区域没有存放实际的数据，存放的是数据记录的地址。数据的存储不是按主键顺序存放的，是按写入的顺序存放。所以找到节点后直接通过数据的地址就能找到数据，比 InnoDB 的搜索效率高。但 MyISAM 的索引不支持事务，所以用的不多。   
>
> ![image-20190706081350305](../images/MyISAM索引.png)

## 锁相关

```text
加锁情况 information_schema.innodb_locks(5.7)&performance_schema.data_locks(8.0)
5.7，通过 information_schema.innodb_locks 查看事务的锁情况，但只能看到阻塞事务的锁；如果事务并未被阻塞，则在该表中看不到该事务的锁情况。
8.0，删除了 information_schema.innodb_locks，添加了 performance_schema.data_locks，不但可以看到阻塞该事务的锁，还可以看到该事务所持有的锁。

read committed 级别用的是纪录锁算法
查询非聚簇索引是对非聚簇索引和主键索引加上相应对 S|X 锁
查询聚簇索引|唯一索引是对聚簇索引加上相应对 S|X 锁
查询非索引是对所有纪录对主键索引加上相应对 S|X 锁

repeatable read 级别用的是next-key算法(gap锁+纪录锁)
查询聚簇索引|唯一索引时会降级成 纪录锁算法
```

## 物化特性仅仅针对查询语句

```sql
-- MySQL5.6 引入了 Materialization 物化特性 
-- 用于子查询（比如在IN/NOT IN子查询以及 FROM 子查询）优化。 
-- 具体实现方式是：在SQL执行过程中，第一次需要子查询结果时执行子查询并将子查询的结果保存为临时表 ，后续对子查询结果集的访问将直接通过临时表获得。
-- 物化子查询优化SQL执行的关键点在于对子查询只需要执行一次。
-- 与之相对的执行方式是对外表的每一行都对子查询进行调用，其执行计划中的查询类型为“DEPENDENT SUBQUERY”。
-- 需要特别注意它目前仅仅针对查询语句的优化。
-- 1. 对待EXISTS子句时，仍然采用嵌套子查询的执行方式。
-- 2. 对于更新或删除需要手工重写成JOIN。

-- 子查询 -> join 
-- 子查询
update operation o
set status = 'applying'
where o.id in (select id
               from (select o.id, o.status
                     from operation o
                     where o.group = 123
                       and o.status not in ('done')
                     order by o.parent,
                              o.id
                     limit 1) t); 
-- join           
update operation o
join (select o.id, o.status
      from operation o
      where o.group = 123
        and o.status not in ('done')
      order by o.parent,
               o.id
      limit 1) t
on o.id = t.id
set status = 'applying';

-- exists -> join
-- exists
SELECT *
FROM my_neighbor n
       LEFT JOIN my_neighbor_apply sra ON n.id = sra.neighbor_id
                                            AND sra.user_id = 'xxx'
WHERE n.topic_status < 4
  AND EXISTS(SELECT 1 FROM message_info m WHERE n.id = m.neighbor_id
                                            AND m.user = 'xxx')
  AND n.topic_type <> 5 ;
-- join 
SELECT *
FROM my_neighbor n
       INNER JOIN message_info m ON n.id = m.neighbor_id
                                      AND m.user = 'xxx'
       LEFT JOIN my_neighbor_apply sra ON n.id = sra.neighbor_id
                                            AND sra.user_id = 'xxx'
WHERE n.topic_status < 4
  AND n.topic_type <> 5 ;
```




