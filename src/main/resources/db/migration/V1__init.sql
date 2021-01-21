CREATE TABLE users
(
    id         bigserial primary key not null,
    name       VARCHAR(50),
    email      VARCHAR(50),
    password   VARCHAR(50)
);

CREATE TABLE items
(
    id         bigserial primary key not null,
    title      VARCHAR(255),
    user_id    bigint references users(id) not null
);

CREATE TABLE auctions
(
    id             bigserial primary key not null,
    date_created   timestamp without time zone,
    done           boolean,
    initial_bid    double precision,
    item_id        bigint references items(id) not null
);

CREATE TABLE bids
(
    id         bigserial primary key not null,
    bid        double precision,
    date       timestamp without time zone,
    user_id    bigint references users(id) not null,
    auction_id bigint references auctions(id) not null
);
