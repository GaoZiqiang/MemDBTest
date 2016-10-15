drop database if exists MemDBTest;
create database MemDBTest;
use MemDBTest;

SET max_heap_table_size = 1024*1024*2000;
CREATE TABLE InsertTable (
`id` int(11) NOT NULL auto_increment,
`name` varchar(50) default NULL,
PRIMARY KEY (`id`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;


delimiter @
create procedure insert_FirstTable(in item integer)
begin
declare counter int;
set counter = item;
while counter >= 1 do
insert into InsertTable values(counter,concat('Record.',counter));
set counter = counter - 1;
end while;
end
@

delimiter @
call insert_FirstTable(10000000);
@
