-- liquibase formatted sql
-- changeset BorkowskiMaciej:2

CREATE TABLE IF NOT EXISTS app.relationship
(
    id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    sender     UUID      NOT NULL,
    recipient  UUID      NOT NULL,
    status     VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
