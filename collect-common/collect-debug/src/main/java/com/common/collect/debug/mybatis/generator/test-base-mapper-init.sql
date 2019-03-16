
drop database if exists `test_base_mapper`;
create database test_base_mapper;

use test_base_mapper;

drop table if exists `test`;
create table `test` (
  `id` bigint(20) not null auto_increment,
  `string_type` varchar(100) comment 'varchar(100)',
  `char_type` char(6) comment 'char',
  `mediumtext_type` mediumtext comment 'mediumtext',
  `datetime_type` datetime comment 'datetime',
  `tinyint_type` tinyint comment 'tinyint',
  `smallint_type` smallint comment 'smallint',
  `mediumint_type` mediumint comment 'mediumint',
  `int_type` int comment 'int',
  `bigint_type` bigint comment 'bigint',
  `double_type` double comment 'double',
  `decimal_type` decimal(3,2) comment 'decimal(3,1)',
  `bit_type` bit comment 'bit',
  `date_type` date comment 'date',
  `create_at` datetime default current_timestamp not null comment '创建时间',
	`update_at` datetime default current_timestamp not null on update current_timestamp comment '修改时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='测试表' ;