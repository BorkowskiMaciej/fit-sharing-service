-- liquibase formatted sql
-- changeset BorkowskiMaciej:8

ALTER TABLE app.public_key
    ADD COLUMN device_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';
