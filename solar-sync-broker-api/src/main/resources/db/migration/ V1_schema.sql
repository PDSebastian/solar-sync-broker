create table agent
(
    id bigint not null
        primary key
);

create table agent_seq
(
    next_val bigint null
);

create table command_executions
(
    id           bigint auto_increment
        primary key,
    command_type varchar(255) not null,
    created_at   datetime(6)  not null,
    executed_by  varchar(255) not null,
    house_id     bigint       not null,
    status       varchar(255) not null,
    target_soc   int          not null
);

create table financial_transaction
(
    id               bigint auto_increment
        primary key,
    amount_kwh       double       null,
    house_id         bigint       null,
    timestamp        datetime(6)  null,
    total_value      double       null,
    transaction_type varchar(255) null,
    unit_price       double       null
);

create table users
(
    id         bigint auto_increment
        primary key,
    age        int                                         not null,
    email      varchar(100)                                not null,
    first_name varchar(100)                                not null,
    last_name  varchar(100)                                not null,
    password   varchar(100)                                not null,
    user_type  enum ('ADMIN', 'AGENT', 'OPERATOR', 'USER') not null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email)
);

create table houses
(
    id                  bigint auto_increment
        primary key,
    enabled             bit          not null,
    max_export_power_kw double       not null,
    max_import_power_kw double       not null,
    name                varchar(100) not null,
    pv_peak_power_kw    double       not null,
    owner_id            bigint       not null,
    constraint FKn9yr5cdkajfhcbtrwnvq8seyl
        foreign key (owner_id) references users (id)
);

create table bateries
(
    id                     bigint auto_increment
        primary key,
    efficienty_percent     double not null,
    max_charge_power_kw    double not null,
    max_discharge_power_kw double not null,
    soc_percent            double not null,
    house_id               bigint not null,
    constraint UKl07ymqyeq29i1intlss9pfwgd
        unique (house_id),
    constraint FK755b1o98wudvikchygbllc6jm
        foreign key (house_id) references houses (id)
);

create table telemetry
(
    id                  bigint auto_increment
        primary key,
    battery_power_kw    double      not null,
    battery_soc_percent double      not null,
    grid_power_kw       double      not null,
    load_power_kw       double      not null,
    local_date_time     datetime(6) not null,
    pv_power_kw         double      not null,
    house_id            bigint      not null,
    constraint FKjnup2cbkxdkk89xthbi5ys3yj
        foreign key (house_id) references houses (id)
);

create table user_permissions
(
    user_id    bigint                                                                                                     not null,
    permission enum ('BATTERY_COMMAND', 'BATTERY_CONFIG', 'BATTERY_MANAGE', 'BATTERY_VIEW', 'HOUSE_MANAGE', 'HOUSE_VIEW') null,
    constraint FKkowxl8b2bngrxd1gafh13005u
        foreign key (user_id) references users (id)
);
