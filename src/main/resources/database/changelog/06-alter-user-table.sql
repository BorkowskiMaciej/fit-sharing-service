-- liquibase formatted sql
-- changeset BorkowskiMaciej:6

ALTER TABLE app.user
    ADD COLUMN date_of_birth DATE NOT NULL DEFAULT '2000-01-01',
    ADD COLUMN gender VARCHAR(10) NOT NULL DEFAULT 'OTHER',
    DROP COLUMN age;

