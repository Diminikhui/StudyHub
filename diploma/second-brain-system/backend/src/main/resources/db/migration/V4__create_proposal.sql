create table if not exists proposal (
                                        id bigserial primary key,
                                        raw_item_id uuid not null,
                                        proposal_type varchar(50) not null,
    status varchar(50) not null,
    title varchar(255) not null,
    description text,
    payload_json text,
    created_at timestamp not null,
    updated_at timestamp not null,

    constraint fk_proposal_raw_item
    foreign key (raw_item_id)
    references raw_item (id)
    on delete cascade
    );