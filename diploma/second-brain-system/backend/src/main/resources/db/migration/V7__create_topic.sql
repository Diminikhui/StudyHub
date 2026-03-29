create table if not exists topic (
                                     id bigserial primary key,
                                     name varchar(255) not null,
    normalized_name varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null
    );