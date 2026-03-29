create table if not exists fact (
                                    id bigserial primary key,
                                    raw_item_id uuid not null,
                                    content_text text not null,
                                    created_at timestamp not null,
                                    updated_at timestamp not null,

                                    constraint fk_fact_raw_item
                                    foreign key (raw_item_id)
    references raw_item (id)
    on delete cascade
    );