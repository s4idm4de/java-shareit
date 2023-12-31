DROP TABLE if exists comments;
DROP TABLE if exists bookings;
DROP TABLE if exists items;
DROP TABLE if exists requests;
DROP TABLE if exists users;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR,
    requestor_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT,
    name VARCHAR,
    description VARCHAR,
    available BOOLEAN,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR,
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR,
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id)
);

