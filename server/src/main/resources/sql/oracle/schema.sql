drop table ss_user;



-- --------------------- 01 ÏµÍ³±í -------------------------------
create table sys_user(
  id number(19,0),
  username  varchar2(100) not null,
  email  varchar2(100),
  mobile_phone_number  varchar2(20),
  password  varchar2(100) not null,
  salt       varchar2(10),
  create_date date,
  status    varchar2(50),
  deleted   bool,
  admin     bool,
  primary key (id)
)