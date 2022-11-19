create table if not exists field (
    id bigint not null auto_increment,
    name varchar(255) not null,
    selector varchar(255) not null,
    weight float not null,
    primary key (id)
) engine=MyISAM;

create table if not exists indexes (
    id bigint not null auto_increment,
    lemma_id bigint not null,
    page_id bigint not null,
    rang float not null,
    primary key (id)
) engine=MyISAM;

create table if not exists lemma (
    id integer not null auto_increment,
    frequency integer not null,
    lemma varchar(50) not null,
    primary key (id)
) engine=MyISAM;

create table if not exists page (
id bigint not null auto_increment,
    code integer not null,
    content mediumtext not null,
    path varchar(200) not null,
    site_id bigint not null,
    primary key (id)
) engine=MyISAM;

create table if not exists site (
id bigint not null auto_increment,
    last_error mediumtext,
    name varchar(255) not null,
    status varchar(255) not null,
    status_time datetime not null,
    url varchar(50) not null,
    primary key (id)
) engine=MyISAM;

alter table indexes add constraint indexes_page_lemma_key unique (page_id, lemma_id);

create index l_index on lemma (lemma);

alter table lemma add constraint lemma_lemma_key unique (lemma);

alter table page add constraint page_path_siteid_key unique (path, site_id);

alter table site add constraint site_url_key unique (url);

alter table indexes add constraint indexes_lemma_fk foreign key (lemma_id) references lemma (id);

alter table indexes add constraint indexes_page_fk foreign key (page_id) references page (id);

alter table page add constraint page_site_fk foreign key (site_id) references site (id);
