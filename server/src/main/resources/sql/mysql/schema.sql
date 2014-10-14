drop table if exists sys_user;



-- --------------------- 01 ÏµÍ³±í -------------------------------
create table `sys_user`(
  `id`         bigint not null auto_increment,
  `username`   varchar(100) not null,
  `email`  varchar(100),
  `mobile_phone_number`  varchar(20),
  `password`  varchar(100) not null,
  `salt`       varchar(10),
  `create_date` timestamp default 0,
  `status`    varchar(50),
  `deleted`   bool,
  `admin`     bool,
  constraint `pk_sys_user` primary key(`id`),
  constraint `unique_sys_user_username` unique(`username`),
  constraint `unique_sys_user_email` unique(`email`),
  constraint `unique_sys_user_mobile_phone_number` unique(`mobile_phone_number`),
  index `idx_sys_user_status` (`status`)
) charset=utf8 ENGINE=InnoDB;;
alter table `sys_user` auto_increment=1000;;