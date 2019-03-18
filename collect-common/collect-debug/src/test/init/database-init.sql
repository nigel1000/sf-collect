
-- 修改密码
use mysql;
update user set password=password("#/d5)anzaVlN") where user='root';
flush privileges;

-- 创建数据库
drop database if exists `test_base_mapper`;
create database test_base_mapper;

use test_base_mapper;