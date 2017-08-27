create table trace_span(
       obj_id varchar2(50) primary key not null,
       trace_id varchar2(50) not null,
       trace_name varchar2(100),
       span_id varchar2(50) not null,
       span_name varchar2(50) not null,
       span_parentid varchar2(50),
       type varchar2(10) not null,
       host varchar2(20) not null,
       timestamp timestamp not null,
       annotation clob
       );