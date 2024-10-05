-- liquibase formatted sql
-- changeset BorkowskiMaciej:3

ALTER TABLE app.user
    ADD COLUMN first_name  VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN last_name   VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN age         INT          NOT NULL DEFAULT 0,
    ADD COLUMN description VARCHAR(255) NOT NULL DEFAULT '';

