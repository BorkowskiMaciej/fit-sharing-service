-- liquibase formatted sql
-- changeset BorkowskiMaciej:7

ALTER TABLE app.user
    ADD COLUMN profile_picture BYTEA;
