drop table if exists entries;
create table entries (
  id integer primary key autoincrement,
  class text not null,
  filename text not null,
  coord1 integer,
  coord2 integer,
  coord3 integer,
  coord4 integer
);