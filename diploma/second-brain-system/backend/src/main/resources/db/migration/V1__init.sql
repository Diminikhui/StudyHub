create table if not exists raw_item (
                                        id uuid primary key,
                                        content_text text not null,
                                        source_type varchar(50) not null,
    status varchar(50) not null,
    created_at timestamp not null,
    updated_at timestamp not null
    );