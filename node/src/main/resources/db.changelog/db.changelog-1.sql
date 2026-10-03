--liquibase formatted sql

--changeset dgilev:1
CREATE TABLE raw_data (
    id BIGSERIAL PRIMARY KEY,
    event JSONB
);

--changeset dgilev:2
CREATE TABLE binary_content (
    id BIGSERIAL PRIMARY KEY,
    file_as_array_of_bytes BYTEA
);

--changeset dgilev:3
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT,
    first_login_date TIMESTAMP,
    first_name VARCHAR,
    last_name VARCHAR,
    username VARCHAR,
    email VARCHAR,
    is_active BOOLEAN,
    state VARCHAR
);

--changeset dgilev:4
CREATE TABLE app_photo (
    id BIGSERIAL PRIMARY KEY,
    telegram_file_id VARCHAR,
    binary_content_id BIGINT REFERENCES binary_content(id),
    file_size BIGINT
);

--changeset dgilev:5
CREATE TABLE app_document (
    id BIGSERIAL PRIMARY KEY,
    telegram_file_id VARCHAR,
    doc_name VARCHAR,
    binary_content_id BIGINT REFERENCES binary_content(id),
    mime_type VARCHAR,
    file_size BIGINT
);