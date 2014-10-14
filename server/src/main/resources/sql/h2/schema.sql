drop table if exists sys_user;



-- --------------------- 01 ÏµÍ³±í -------------------------------
create table sys_user(
  id        bigint not null auto_increment,
  username  varchar(100) not null,
  email  varchar(100),
  mobile_phone_number  varchar(20),
  password  varchar(100) not null,
  salt       varchar(10),
  create_date timestamp default 0,
  status    varchar(50),
  deleted   bool,
  admin     bool,
  primary key (id)
)