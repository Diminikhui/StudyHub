alter table action_item
    add column topic_id bigint;

alter table action_item
    add constraint fk_action_item_topic
        foreign key (topic_id)
            references topic (id)
            on delete set null;