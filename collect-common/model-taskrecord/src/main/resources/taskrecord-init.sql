
drop table if exists `task_record`;
create table `task_record` (
  `id` bigint(20) not null auto_increment,
  `biz_type` varchar(127) not null comment '业务类型',
  `biz_id` varchar(127) not null default '' comment '业务 id',
  `body` mediumtext comment '消息体',
  `extra` mediumtext comment '譬如tag，key，集群ip，port等',
  `try_times` tinyint(3) not null default 0 comment '尝试次数',
  `max_try_times` tinyint(3) not null default 3 comment '最大尝试次数 默认三次',
  `state` tinyint(3) not null default 0 comment '状态 0：失败 1：成功',
  `alert_type` varchar(20) comment '通知方式',
  `alert_target` varchar(512)  comment '通知目标',
  `first_error_message` mediumtext comment '消费时的错误信息',
  `last_error_message` mediumtext comment '最后一次重试的错误信息',
  `create_at` datetime default current_timestamp not null comment '创建时间',
  `update_at` datetime default current_timestamp not null on update current_timestamp comment '修改时间',
  index idx_state (biz_type,state,id),
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='重试纪录表' ;
