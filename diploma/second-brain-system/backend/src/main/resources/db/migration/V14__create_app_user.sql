create table app_user (
                          id bigserial primary key,
                          username varchar(100) not null unique,
                          password_hash varchar(255) not null,
                          role varchar(20) not null,
                          created_at timestamp not null,
                          updated_at timestamp not null
);