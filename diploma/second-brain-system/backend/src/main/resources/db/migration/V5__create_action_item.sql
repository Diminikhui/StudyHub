create table if not exists action_item (
                                           id bigserial primary key,
                                           raw_item_id uuid not null,
                                           title varchar(255) not null,
    done boolean not null default false,
    created_at timestamp not null,
    updated_at timestamp not null,

    constraint fk_action_item_raw_item
    foreign key (raw_item_id)
    references raw_item (id)
    on delete cascade
    );