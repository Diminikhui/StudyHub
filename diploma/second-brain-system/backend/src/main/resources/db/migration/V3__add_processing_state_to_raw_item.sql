alter table raw_item
    add column processing_state varchar(50) not null default 'PENDING';