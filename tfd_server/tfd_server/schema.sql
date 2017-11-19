drop table if exists entries;
create table entries (
  id integer primary key autoincrement,
  class text not null,
  title text not null,
  'text' text not null
);