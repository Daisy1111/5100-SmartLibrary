# --- !Ups

create sequence smartlibrary_sequence;

create table book (
	book_id bigint not null default nextval('smartlibrary_sequence') primary key,
	title varchar(260) not null,
	description text,
	publisher varchar(200),
	date_published timestamp,
	page_count int,
	contact_information text,
	date_created datetime not null default now(),
	date_modified datetime not null default now()
);

create table author (
	author_id bigint not null default nextval('smartlibrary_sequence') primary key,
	name varchar(100) not null unique
);

create table tag (
    tag_id bigint not null default nextval('smartlibrary_sequence') primary key,
    name varchar(30) not null unique
);

create table book_author (
    book_id bigint not null references book on delete cascade,
    author_id bigint not null references author on delete cascade
);

create table book_tag (
    book_id bigint not null references book on delete cascade,
    tag_id bigint not null references tag on delete cascade
);


# --- !Downs
drop table book_tag;
drop table book_author;
drop table tag;
drop table author;
drop table book;
drop sequence smartlibrary_sequence;
