-- V1: schema initiala (MySQL 8.4), generata din entitatile JPA existente.
-- Numele coloanelor urmeaza entitatile exact (ex. efficienty_percent), ca ddl-auto: validate sa treaca.
-- Pe o baza veche, creata de Hibernate, Flyway marcheaza schema la versiunea 1 (baseline-on-migrate)
-- si nu ruleaza acest fisier; pe o baza goala il ruleaza integral.

create table users (
    id         bigint       not null auto_increment,
    age        integer      not null,
    email      varchar(100) not null,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    password   varchar(100) not null,
    user_type  enum ('ADMIN','AGENT','OPERATOR','USER') not null,
    primary key (id),
    constraint uq_users_email unique (email)
) engine=InnoDB;

create table user_permissions (
    user_id    bigint not null,
    permission enum ('BATTERY_COMMAND','BATTERY_CONFIG','BATTERY_MANAGE','BATTERY_VIEW','FINANCIAL_MANAGE','FINANCIAL_VIEW','GRID_VIEW','HOUSE_MANAGE','HOUSE_VIEW','USER_MANAGE')
) engine=InnoDB;

create table houses (
    id                  bigint       not null auto_increment,
    battery_id          bigint,
    owner_id            bigint       not null,
    name                varchar(100) not null,
    enabled             bit          not null,
    pv_peak_power_kw    float(53)    not null,
    max_import_power_kw float(53)    not null,
    max_export_power_kw float(53)    not null,
    primary key (id),
    constraint uq_houses_battery unique (battery_id)
) engine=InnoDB;

create table bateries (
    id                     bigint    not null auto_increment,
    house_id               bigint    not null,
    soc_percent            float(53) not null,
    max_charge_power_kw    float(53) not null,
    max_discharge_power_kw float(53) not null,
    efficienty_percent     float(53) not null,
    primary key (id),
    constraint uq_bateries_house unique (house_id)
) engine=InnoDB;

create table command_executions (
    id                bigint       not null auto_increment,
    house_id          bigint       not null,
    command_type      varchar(255) not null,
    power_kw          float(53)    not null,
    status            varchar(255) not null,
    failure_reason    varchar(255),
    soc_after_percent float(53),
    executed_by       varchar(255) not null,
    created_at        datetime(6)  not null,
    primary key (id)
) engine=InnoDB;

create table telemetry (
    id                  bigint      not null auto_increment,
    house_id            bigint      not null,
    local_date_time     datetime(6) not null,
    pv_power_kw         float(53)   not null,
    load_power_kw       float(53)   not null,
    grid_power_kw       float(53)   not null,
    battery_power_kw    float(53)   not null,
    battery_soc_percent float(53)   not null,
    primary key (id)
) engine=InnoDB;

create table financial_transaction (
    id               bigint not null auto_increment,
    house_id         bigint,
    transaction_type varchar(255),
    amount_kwh       float(53),
    unit_price       float(53),
    total_value      float(53),
    timestamp        datetime(6),
    primary key (id)
) engine=InnoDB;

-- Chei straine dupa creare: houses <-> bateries se refera una la alta.
alter table user_permissions
    add constraint fk_user_permissions_user foreign key (user_id) references users (id);

alter table houses
    add constraint fk_houses_owner foreign key (owner_id) references users (id);

alter table houses
    add constraint fk_houses_battery foreign key (battery_id) references bateries (id);

alter table bateries
    add constraint fk_bateries_house foreign key (house_id) references houses (id);

alter table command_executions
    add constraint fk_command_executions_house foreign key (house_id) references houses (id);

alter table telemetry
    add constraint fk_telemetry_house foreign key (house_id) references houses (id);
