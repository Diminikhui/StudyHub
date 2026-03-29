alter table fact
    add column topic_id bigint;

alter table fact
    add constraint fk_fact_topic
        foreign key (topic_id)
            references topic (id)
            on delete set null;