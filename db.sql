create table trace_span(
       trace_id varchar2(50) primary key,
       trace_name varchar2(100),
       span_id varchar2(50),
       span_name varchar2(50),
       span_parentid varchar2(50),
       type number(2),
       host varchar2(20),
       timestamp timestamp,
       annotation clob
       );