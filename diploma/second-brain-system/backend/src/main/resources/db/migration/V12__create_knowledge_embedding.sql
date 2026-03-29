CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE knowledge_embedding (
                                     id bigserial primary key,
                                     entity_type varchar(32) not null,
                                     entity_id bigint not null,
                                     source_text text not null,
                                     embedding vector(1536) not null,
                                     created_at timestamp not null,
                                     updated_at timestamp not null
);