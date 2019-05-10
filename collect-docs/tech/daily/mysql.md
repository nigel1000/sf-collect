## 技巧 sql
```sql
-- 批量删除 table
select concat( 'drop table ', table_name, ';' ) from information_schema.tables where table_name like 'hotel_%';

-- 筛选商品某个规格上的某些属性不一致的商品id
select distinct(goods_id) from (
  select goods_id, sku_id, 
    concat_ws(',', ifnull(cost_price, '-'), ifnull(market_price, '-'), ifnull(sale_price, '-'), ifnull(rake_off, '-')) as key2
  from goods_online
  where if_del = '0' group by goods_id, sku_id having min(key2) != max(key2)
) as t1 limit 5000

-- 日期处理
-- 在mysql 数据库中，“2009-09-15 00：00：00”转化为列为长整型的函数：
select * from tb where createAt < unix_timestamp("2013-03-15 00:00:00") * 1000,
-- 在mysql数据库中，“1252999488000”（java中的long型数据）转化为日期：【注】：要将最后三位去掉。
select  * from tb where createAt <  from_unixtime(1252999488);

-- mysql 引擎优化导致没有或者选错索引，基数(索引的区分度&采样统计)和多索引的情况下
-- 强制使用索引
select * from t force index(idx_c) where c < 100 and c < 100000;
-- 查询索引的基数和实际是否符合
show index from t;
-- 如果和实际很不符合的话，可以重新来统计索引的基数
analyze table t;

-- 查看优化后的 sql
explain extended select * from t;
show warnings;
```

## 创建表
```sql
drop table if exists `demo`;
create table `demo` (
  `id` bigint(20) not null auto_increment,
  `biz_type` varchar(31) not null comment '操作类型',
  `before_value` mediumtext comment '先前的值',
  `create_at` datetime default current_timestamp not null comment '创建时间',
  `update_at` datetime default current_timestamp not null on update current_timestamp comment '修改时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='demo 表' ;
```

## 索引操作
```sql
create unique index uniq_category on `category` (`first_category_id`,`second_category_id`);
create index idx_sku_storage on `goods_sku_store` (sku_id, store_id);
drop index idx_sku_storage on `goods_sku_manage`;
```

## 修改表结构
```sql
alter table table_name convert to character set utf8mb4 collate utf8mb4_general_ci; 

alter table `flash_sale_goods_online`
  modify `goods_title_new` varchar(512) not null default '' comment '自定义商品标题',
  modify `goods_tag_new` varchar(512) not null default '' comment '自定义商品标签',
  add column `goods_activity_pics_new` varchar(512) not null default '' comment '自定义商品活动图url',
  add column `goods_ten_desc_new` varchar(512) not null default '' comment '自定义商品十字描述',
  add column `goods_tag_pic_new` varchar(512) not null default '' comment '自定义商品图片标签';
```

## 通用操作
```sql
-- 连接数据库实例
mysql -p 3306 -h `127.0.0.1` -uroot -p123456
-- 创建数据库
create database sf-house default character set utf8mb4 collate utf8mb4_general_ci;
-- 导入数据
source ~/Documents/zcy_develop.sql;
-- 修改密码
use mysql;
update user set password=password("#/d5)anzaVlN") where user='root';
flush privileges;
-- 所有大于16M的SQL文件都会报ERROR 2006 (HY000) at line 17128: MySQL server has gone away，可以登录MySQL客户端，修改系统变量：
set global max_allowed_packet=500*1024*1024;
```

## 系统操作
```sql
-- 视图表 information_schema.views
-- 触发器 information_schema.triggers

-- 显示变量值 
show variables like '%max_allowed_packet%'
show status like 'slow_queries';
-- 查看当前所有的连接数和连接状态
show full processlist;
-- 查看死锁日志，引擎当前状态
show engine innodb status;
-- 查询版本号
select version();
-- 查看建表语句
show create table test_base_mapper.flow_log;
desc test_base_mapper.flow_log;
-- 事务隔离级别
select @@tx_isolation;
set session transaction isolation level repeatable read;
```

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


