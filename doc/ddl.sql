-- auto-generated definition
create table receive_msg
(
    id               bigint auto_increment
        primary key,
    unique_key       varchar(128)                        not null,
    receive_time     bigint(13)                          not null,
    push_body        text                                not null,
    push_tag         varchar(128)                        not null,
    push_topic       varchar(128)                        not null,
    expect_push_time bigint(13)                          not null,
    create_time      timestamp default CURRENT_TIMESTAMP not null,
    status           int                                 not null,
    resp             varchar(512)                        null,
    real_push_time   bigint(13)                          null,
    constraint receive_msg_unique_key_uindex
        unique (unique_key)
);

create index receive_msg_expect_push_time_index
    on receive_msg (expect_push_time);

create index receive_msg_statue_index
    on receive_msg (statue);

