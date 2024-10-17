-- liquibase formatted sql
-- changeset BorkowskiMaciej:5

CREATE TABLE app.public_key
(
    id           UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    fs_user_id   UUID      NOT NULL,
    key          BYTEA     NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);