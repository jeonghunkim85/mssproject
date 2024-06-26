create table brands
(
    id         bigint auto_increment,
    name       varchar(255),
    created_at timestamp(6) default current_timestamp(),
    updated_at timestamp(6) default current_timestamp(),
    primary key (id)
);

create table categories
(
    id         bigint auto_increment,
    name       varchar(255),
    created_at timestamp(6) default current_timestamp(),
    updated_at timestamp(6) default current_timestamp(),
    primary key (id)
);

create table products
(
    id          bigint auto_increment,
    price       numeric(40, 0),
    brand_id    bigint not null,
    category_id bigint not null,
    created_at timestamp(6) default current_timestamp(),
    updated_at timestamp(6) default current_timestamp(),
    primary key (id)
);


create unique index idx_categories_01 on categories (name);
create index idx_products_01 on products (category_id, price);
create index idx_products_02 on products (brand_id);
create unique index idx_brands_01 on brands (name);