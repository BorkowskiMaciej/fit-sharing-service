-- liquibase formatted sql
-- changeset BorkowskiMaciej:4

ALTER TABLE app.user
    ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'admin';

