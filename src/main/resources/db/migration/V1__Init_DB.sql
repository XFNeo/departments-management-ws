create sequence hibernate_sequence start 5 increment 1;

create table department (
    id int8 not null,
    name varchar(255),
    primary key (id)
);

insert into department (id, name)
values (1, 'IT Department'),
       (2, 'QA Department'),
       (3, 'Accounting Department'),
       (4, 'Development Department');