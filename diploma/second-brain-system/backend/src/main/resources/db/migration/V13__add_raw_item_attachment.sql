alter table raw_item
    alter column content_text drop not null;

create table raw_item_attachment (
                                     id bigserial primary key,
                                     raw_item_id uuid not null references raw_item(id) on delete cascade,
                                     original_file_name varchar(255) not null,
                                     stored_file_name varchar(255) not null,
                                     mime_type varchar(255),
                                     file_size bigint not null,
                                     storage_path text not null,
                                     created_at timestamp not null,
                                     updated_at timestamp not null
);

create index idx_raw_item_attachment_raw_item_id
    on raw_item_attachment(raw_item_id);