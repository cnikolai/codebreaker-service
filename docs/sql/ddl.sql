create table user_profile
(
    user_id      UUID         not null,
    created      timestamp    not null,
    display_name varchar(100) not null,
    external_key UUID         not null,
    oauth_key    varchar(30)  not null,
    primary key (user_id)
);
create index IDXakmwux4w2swsj69pg3ignha1v on user_profile (created);
alter table user_profile
    add constraint UK_j35xlx80xoi2sb176qdrtoy69 unique (display_name);
alter table user_profile
    add constraint UK_22o8v4jg08yk7piojnowil30o unique (external_key);
alter table user_profile
    add constraint UK_6f815wi5o4jq8p1q1w63o4mhd unique (oauth_key);

