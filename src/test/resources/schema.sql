Drop table if exists bookings;
Drop table if exists requests;
Drop table if exists comments;
Drop table if exists items;
Drop table if exists users;


CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(320) NOT NULL,
    name varchar(100) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL,
    description varchar(320) NOT NULL,
    is_available Boolean NOT NULL,
    owner_id BIGINT NOT NULL,
    url VARCHAR(1000),
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id), UNIQUE(id, url)
    );

CREATE TABLE IF NOT EXISTS bookings(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE not null ,
    end_date   TIMESTAMP WITHOUT TIME ZONE not null ,
    booker_id  BIGINT not null ,
    item_id    bigint not null ,
    status     varchar(8),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id)
);

create table if not exists requests(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  varchar(320) not null ,
    requestor_id bigint not null ,
    created      TIMESTAMP WITHOUT TIME ZONE not null,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(320) not null ,
    item_id bigint not null ,
    author_id bigint not null ,
    created TIMESTAMP WITHOUT TIME ZONE not null ,
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id)
);


