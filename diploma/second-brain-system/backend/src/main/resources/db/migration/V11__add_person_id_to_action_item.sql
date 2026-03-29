alter table action_item
    add column person_id bigint;

alter table action_item
    add constraint fk_action_item_person
        foreign key (person_id)
            references person (id)
            on delete set null;