create table if not exists raw_fragment (
                                            id bigserial primary key,
                                            raw_item_id uuid not null,
                                            fragment_index integer not null,
                                            content_text text not null,

                                            constraint fk_raw_fragment_raw_item
                                            foreign key (raw_item_id)
    references raw_item (id)
    on delete cascade
    );