-- H2 2.3.232;
;
CREATE USER IF NOT EXISTS "SA" SALT '0676e65fba8516eb' HASH '8499dead3083af86679a97ee296b63d42e9396866fa4397bd7a6d0d993fb7464' ADMIN;
CREATE CACHED TABLE "PUBLIC"."AREA_FEATURES"(
    "AREA_ID" UUID NOT NULL,
    "FEATURES" ENUM('CHANCELLERY', 'CONDITIONER', 'LIGHT', 'PROJECTOR', 'SILENT', 'SPEAKER_SYSTEM', 'TV', 'WHITEBOARD')
);
-- 1 +/- SELECT COUNT(*) FROM PUBLIC.AREA_FEATURES;
INSERT INTO "PUBLIC"."AREA_FEATURES" VALUES
    (UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', 'CHANCELLERY');
CREATE CACHED TABLE "PUBLIC"."AREA_KEYS"(
    "AREA_ID" UUID NOT NULL,
    "KEYS" CHARACTER VARYING(255) NOT NULL
);
-- 3 +/- SELECT COUNT(*) FROM PUBLIC.AREA_KEYS;
INSERT INTO "PUBLIC"."AREA_KEYS" VALUES
                                     (UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', 'arch.png'),
                                     (UUID '3d6c3b24-66f3-41ba-a866-afe5138be62a', 'arch.png'),
                                     (UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', 'arch.png');
CREATE CACHED TABLE "PUBLIC"."AREAS"(
    "ID" UUID NOT NULL,
    "CAPACITY" INTEGER NOT NULL,
    "DESCRIPTION" CHARACTER VARYING(255),
    "NAME" CHARACTER VARYING(255) NOT NULL,
    "STATUS" ENUM('AVAILABLE', 'BOOKED', 'UNAVAILABLE') NOT NULL,
    "TYPE" ENUM('LECTURE_HALL', 'MEETING_ROOM', 'WORKPLACE') NOT NULL
);
ALTER TABLE "PUBLIC"."AREAS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_3" PRIMARY KEY("ID");
-- 3 +/- SELECT COUNT(*) FROM PUBLIC.AREAS;
INSERT INTO "PUBLIC"."AREAS" VALUES
                                 (UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', 4, U&'\0411\043e\043b\044c\0448\0430\044f \043e\0442\043a\0440\044b\0442\0430\044f \0437\043e\043d\0430 \0441 \043e\0431\0449\0438\043c\0438 \0441\0442\043e\043b\0430\043c\0438 \0438 \0443\0434\043e\0431\043d\044b\043c\0438 \0441\0438\0434\0435\043d\044c\044f\043c\0438.', 'Open Space', 'AVAILABLE', 'WORKPLACE'),
                                 (UUID '3d6c3b24-66f3-41ba-a866-afe5138be62a', 10, U&'\041e\0442\0434\0435\043b\044c\043d\0430\044f \043f\0435\0440\0435\0433\043e\0432\043e\0440\043d\0430\044f \043a\043e\043c\043d\0430\0442\0430 \0441 \043f\0440\043e\0435\043a\0442\043e\0440\043e\043c \0438 \043a\043e\043d\0444\0435\0440\0435\043d\0446-\0441\0442\043e\043b\043e\043c.', 'Meeting Room Alpha', 'BOOKED', 'MEETING_ROOM'),
                                 (UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', 10, U&'\0412\044b\0434\0435\043b\0435\043d\043d\043e\0435 \0442\0438\0445\043e\0435 \0440\0430\0431\043e\0447\0435\0435 \043c\0435\0441\0442\043e \0434\043b\044f \0441\043e\0441\0440\0435\0434\043e\0442\043e\0447\0435\043d\043d\043e\0439 \0440\0430\0431\043e\0442\044b.', 'Quiet Zone', 'AVAILABLE', 'MEETING_ROOM');
CREATE CACHED TABLE "PUBLIC"."BOOKINGS"(
    "ID" UUID NOT NULL,
    "CREATED_AT" TIMESTAMP(6) NOT NULL,
    "END_TIME" TIMESTAMP(6) NOT NULL,
    "QUANTITY" INTEGER NOT NULL,
    "START_TIME" TIMESTAMP(6) NOT NULL,
    "STATUS" ENUM('CANCELED', 'COMPLETED', 'CONFIRMED', 'PENDING') NOT NULL,
    "AREA_ID" UUID NOT NULL,
    "USER_ID" UUID NOT NULL
);
ALTER TABLE "PUBLIC"."BOOKINGS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_A" PRIMARY KEY("ID");
-- 12 +/- SELECT COUNT(*) FROM PUBLIC.BOOKINGS;
INSERT INTO "PUBLIC"."BOOKINGS" VALUES
                                    (UUID 'c11475af-afc5-4ddf-8d8a-d3da0f7b0073', TIMESTAMP '2025-01-03 22:39:25.746173', TIMESTAMP '2025-05-25 18:00:00', 1, TIMESTAMP '2025-05-25 16:00:00', 'CONFIRMED', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '7db889a3-7c41-49dc-a464-4232b82ba258', TIMESTAMP '2025-01-03 22:39:25.746173', TIMESTAMP '2025-05-25 18:00:00', 1, TIMESTAMP '2025-05-25 16:00:00', 'CONFIRMED', UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID 'edb316d0-8028-4447-9b8f-1a71c1855776', TIMESTAMP '2025-01-03 22:39:25.746173', TIMESTAMP '2025-05-25 18:00:00', 1, TIMESTAMP '2025-05-25 16:00:00', 'CONFIRMED', UUID '3d6c3b24-66f3-41ba-a866-afe5138be62a', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '33343ed9-c111-4f74-ad34-da0d2fc38167', TIMESTAMP '2025-01-03 22:39:25.746173', TIMESTAMP '2025-01-23 18:00:00', 1, TIMESTAMP '2025-01-23 16:00:00', 'CONFIRMED', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '09f50190-97a0-4c13-98b7-085db25dd1c7', TIMESTAMP '2025-01-03 22:39:25.746173', TIMESTAMP '2025-01-23 18:00:00', 1, TIMESTAMP '2025-01-23 16:00:00', 'CONFIRMED', UUID '3d6c3b24-66f3-41ba-a866-afe5138be62a', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID 'b420214b-340c-4bd6-a068-f912b60f1717', TIMESTAMP '2025-01-03 22:39:25.746173', TIMESTAMP '2025-01-23 18:00:00', 1, TIMESTAMP '2025-01-23 16:00:00', 'CONFIRMED', UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID 'd5cbaab9-355e-42d6-893c-1750ef90f034', TIMESTAMP '2025-04-03 22:39:25.746173', TIMESTAMP '2025-08-19 00:00:00', 1, TIMESTAMP '2025-08-18 22:00:00', 'CONFIRMED', UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID 'be66eedb-a0dd-4626-8aae-506bcc205d69', TIMESTAMP '2025-04-03 22:39:25.746173', TIMESTAMP '2025-08-19 00:00:00', 1, TIMESTAMP '2025-08-18 22:00:00', 'CONFIRMED', UUID '3d6c3b24-66f3-41ba-a866-afe5138be62a', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '86ef5a0f-7f26-40a8-ae8d-38f097936053', TIMESTAMP '2025-04-03 22:39:25.746173', TIMESTAMP '2025-08-19 00:00:00', 1, TIMESTAMP '2025-08-18 22:00:00', 'CANCELED', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '3fda224e-44ce-45ee-833a-3fffa3233808', TIMESTAMP '2025-08-18 23:08:55.861206', TIMESTAMP '2025-08-19 11:00:00', 0, TIMESTAMP '2025-08-19 10:00:00', 'CONFIRMED', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '5230dfc7-76ab-4f24-bbd3-b485b93c64b2', TIMESTAMP '2025-08-18 23:08:55.86908', TIMESTAMP '2025-08-20 19:00:00', 0, TIMESTAMP '2025-08-20 18:00:00', 'CONFIRMED', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                    (UUID '8b244cc6-39e4-47dc-bd52-2a8eba4f5cbd', TIMESTAMP '2025-08-18 23:08:55.877014', TIMESTAMP '2025-08-20 21:00:00', 0, TIMESTAMP '2025-08-20 20:00:00', 'CONFIRMED', UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7');
CREATE CACHED TABLE "PUBLIC"."EVENT_FORMATS"(
    "EVENT_ID" UUID NOT NULL,
    "FORMAT" ENUM('GAMES', 'HACKATHON', 'LECTURE', 'MEETUP', 'NETWORKING', 'RELAX', 'WORKSHOP')
);
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_FORMATS;
CREATE CACHED TABLE "PUBLIC"."EVENT_KEYS"(
    "EVENT_ID" UUID NOT NULL,
    "KEYS" CHARACTER VARYING(255) NOT NULL
);
-- 3 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_KEYS;
INSERT INTO "PUBLIC"."EVENT_KEYS" VALUES
                                      (UUID '661e23a6-135c-4558-bf0d-8dc7ac6cce75', 'arch.png'),
                                      (UUID '8c9ddfcf-3835-4e77-aa0a-8ff771779865', 'arch.png'),
                                      (UUID 'ba7d8e60-a81a-46ad-9d64-4992abaf33c0', 'arch.png');
CREATE CACHED TABLE "PUBLIC"."EVENT_NOTIFICATIONS"(
    "ID" UUID NOT NULL,
    "EVENT_DATE_TIME" TIMESTAMP(6),
    "EVENT_ID" UUID NOT NULL,
    "EVENT_TITLE" CHARACTER VARYING(255),
    "MESSAGE" CHARACTER VARYING(255),
    "USER_EMAIL" CHARACTER VARYING(255),
    "USER_ID" UUID NOT NULL
);
ALTER TABLE "PUBLIC"."EVENT_NOTIFICATIONS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_4" PRIMARY KEY("ID");
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_NOTIFICATIONS;
CREATE CACHED TABLE "PUBLIC"."EVENT_PARTICIPATION_FORMATS"(
    "EVENT_ID" UUID NOT NULL,
    "PARTICIPATION_FORMAT" ENUM('HYBRID', 'INDIVIDUAL', 'OFFLINE', 'ONLINE', 'RECORDING')
);
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_PARTICIPATION_FORMATS;
CREATE CACHED TABLE "PUBLIC"."EVENT_TAGS"(
    "EVENT_ID" UUID NOT NULL,
    "TAG" ENUM('ART', 'BUSINESS', 'IT', 'MARKETING', 'PSYCHOLOGY', 'SCIENCE', 'SUCCESS_STORY', 'TECHNOLOGY')
);
-- 6 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_TAGS;
INSERT INTO "PUBLIC"."EVENT_TAGS" VALUES
                                      (UUID '661e23a6-135c-4558-bf0d-8dc7ac6cce75', 'TECHNOLOGY'),
                                      (UUID '661e23a6-135c-4558-bf0d-8dc7ac6cce75', 'IT'),
                                      (UUID '8c9ddfcf-3835-4e77-aa0a-8ff771779865', 'SCIENCE'),
                                      (UUID '8c9ddfcf-3835-4e77-aa0a-8ff771779865', 'IT'),
                                      (UUID 'ba7d8e60-a81a-46ad-9d64-4992abaf33c0', 'TECHNOLOGY'),
                                      (UUID 'ba7d8e60-a81a-46ad-9d64-4992abaf33c0', 'IT');
CREATE CACHED TABLE "PUBLIC"."EVENT_TIMES"(
    "EVENT_ID" UUID NOT NULL,
    "TIME" ENUM('DAY', 'EVENING', 'MORNING', 'NIGHT', 'WEEKDAYS', 'WEEKENDS')
);
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_TIMES;
CREATE CACHED TABLE "PUBLIC"."EVENT_USERS"(
    "EVENT_ID" UUID NOT NULL,
    "USER_ID" UUID NOT NULL
);
ALTER TABLE "PUBLIC"."EVENT_USERS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_D" PRIMARY KEY("EVENT_ID", "USER_ID");
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.EVENT_USERS;
INSERT INTO "PUBLIC"."EVENT_USERS" VALUES
                                       (UUID '661e23a6-135c-4558-bf0d-8dc7ac6cce75', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7'),
                                       (UUID 'ba7d8e60-a81a-46ad-9d64-4992abaf33c0', UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7');
CREATE CACHED TABLE "PUBLIC"."EVENTS"(
    "ID" UUID NOT NULL,
    "AVAILABLE_PLACES" INTEGER NOT NULL,
    "DESCRIPTION" CHARACTER VARYING(255) NOT NULL,
    "END_TIME" TIMESTAMP(6),
    "NAME" CHARACTER VARYING(255) NOT NULL,
    "START_TIME" TIMESTAMP(6) NOT NULL,
    "AREA_ID" UUID,
    "SYSTEM_BOOKING_ID" UUID
);
ALTER TABLE "PUBLIC"."EVENTS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_7" PRIMARY KEY("ID");
-- 3 +/- SELECT COUNT(*) FROM PUBLIC.EVENTS;
INSERT INTO "PUBLIC"."EVENTS" VALUES
                                  (UUID '661e23a6-135c-4558-bf0d-8dc7ac6cce75', 30, U&'\0412\043e\0437\043c\043e\0436\043d\043e\0441\0442\044c \0434\043b\044f \0441\0442\0430\0440\0442\0430\043f\043e\0432 \043f\0440\0435\0434\0441\0442\0430\0432\0438\0442\044c \0441\0432\043e\0438 \0438\0434\0435\0438 \0438\043d\0432\0435\0441\0442\043e\0440\0430\043c.', TIMESTAMP '2025-09-01 11:00:00', U&'\041d\043e\0447\044c \043f\0440\0435\0437\0435\043d\0442\0430\0446\0438\0439 \0441\0442\0430\0440\0442\0430\043f\043e\0432', TIMESTAMP '2025-09-01 10:00:00', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '3fda224e-44ce-45ee-833a-3fffa3233808'),
                                  (UUID '8c9ddfcf-3835-4e77-aa0a-8ff771779865', 0, U&'\041f\0440\0430\043a\0442\0438\0447\0435\0441\043a\0438\0439 \0441\0435\043c\0438\043d\0430\0440 \043f\043e \0441\043e\0437\0434\0430\043d\0438\044e \043f\0440\0438\043b\043e\0436\0435\043d\0438\0439 \043d\0430 \0431\0430\0437\0435 \0438\0441\043a\0443\0441\0441\0442\0432\0435\043d\043d\043e\0433\043e \0438\043d\0442\0435\043b\043b\0435\043a\0442\0430.', TIMESTAMP '2025-09-01 19:00:00', U&'\041c\0430\0441\0442\0435\0440\0441\043a\0430\044f \0438\0441\043a\0443\0441\0441\0442\0432\0435\043d\043d\043e\0433\043e \0438\043d\0442\0435\043b\043b\0435\043a\0442\0430', TIMESTAMP '2025-09-01 18:00:00', UUID '32c1c2d8-f8b6-44d1-8536-a405ff007c44', UUID '5230dfc7-76ab-4f24-bbd3-b485b93c64b2'),
                                  (UUID 'ba7d8e60-a81a-46ad-9d64-4992abaf33c0', 30, U&'\0412\043e\0437\043c\043e\0436\043d\043e\0441\0442\044c \0434\043b\044f \0441\0442\0430\0440\0442\0430\043f\043e\0432 \043f\0440\0435\0434\0441\0442\0430\0432\0438\0442\044c \0441\0432\043e\0438 \0438\0434\0435\0438 \0438\043d\0432\0435\0441\0442\043e\0440\0430\043c.', TIMESTAMP '2025-09-01 21:00:00', U&'\041c\0430\0441\0442\0435\0440\0441\043a\0430\044f \0438\0441\043a\0443\0441\0441\0442\0432\0435\043d\043d\043e\0433\043e \0438\043d\0442\0435\043b\043b\0435\043a\0442\0430 \0432\043e\0437\0432\0440\0430\0449\0430\0435\0442\0441\044f', TIMESTAMP '2025-09-01 20:00:00', UUID '6cd6422b-bf0c-4b93-9e45-86cf86a9fe0b', UUID '8b244cc6-39e4-47dc-bd52-2a8eba4f5cbd');
CREATE CACHED TABLE "PUBLIC"."HALL_OCCUPANCY"(
    "DATE_TIME" TIMESTAMP(6) NOT NULL,
    "RESERVED_PLACES" INTEGER NOT NULL
);
ALTER TABLE "PUBLIC"."HALL_OCCUPANCY" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_9" PRIMARY KEY("DATE_TIME");
-- 403 +/- SELECT COUNT(*) FROM PUBLIC.HALL_OCCUPANCY;
INSERT INTO "PUBLIC"."HALL_OCCUPANCY" VALUES
                                          (TIMESTAMP '2025-08-18 08:00:00', 0),
                                          (TIMESTAMP '2025-08-18 09:00:00', 0),
                                          (TIMESTAMP '2025-08-18 10:00:00', 0),
                                          (TIMESTAMP '2025-08-18 11:00:00', 0),
                                          (TIMESTAMP '2025-08-18 12:00:00', 0),
                                          (TIMESTAMP '2025-08-18 13:00:00', 0),
                                          (TIMESTAMP '2025-08-18 14:00:00', 0),
                                          (TIMESTAMP '2025-08-18 15:00:00', 0),
                                          (TIMESTAMP '2025-08-18 16:00:00', 0),
                                          (TIMESTAMP '2025-08-18 17:00:00', 0),
                                          (TIMESTAMP '2025-08-18 18:00:00', 0),
                                          (TIMESTAMP '2025-08-18 19:00:00', 0),
                                          (TIMESTAMP '2025-08-18 20:00:00', 0),
                                          (TIMESTAMP '2025-08-19 08:00:00', 0),
                                          (TIMESTAMP '2025-08-19 09:00:00', 0),
                                          (TIMESTAMP '2025-08-19 10:00:00', 0),
                                          (TIMESTAMP '2025-08-19 11:00:00', 0),
                                          (TIMESTAMP '2025-08-19 12:00:00', 0),
                                          (TIMESTAMP '2025-08-19 13:00:00', 0),
                                          (TIMESTAMP '2025-08-19 14:00:00', 0),
                                          (TIMESTAMP '2025-08-19 15:00:00', 0),
                                          (TIMESTAMP '2025-08-19 16:00:00', 0),
                                          (TIMESTAMP '2025-08-19 17:00:00', 0),
                                          (TIMESTAMP '2025-08-19 18:00:00', 0),
                                          (TIMESTAMP '2025-08-19 19:00:00', 0),
                                          (TIMESTAMP '2025-08-19 20:00:00', 0),
                                          (TIMESTAMP '2025-08-20 08:00:00', 0),
                                          (TIMESTAMP '2025-08-20 09:00:00', 0),
                                          (TIMESTAMP '2025-08-20 10:00:00', 0),
                                          (TIMESTAMP '2025-08-20 11:00:00', 0),
                                          (TIMESTAMP '2025-08-20 12:00:00', 0),
                                          (TIMESTAMP '2025-08-20 13:00:00', 0),
                                          (TIMESTAMP '2025-08-20 14:00:00', 0),
                                          (TIMESTAMP '2025-08-20 15:00:00', 0),
                                          (TIMESTAMP '2025-08-20 16:00:00', 0),
                                          (TIMESTAMP '2025-08-20 17:00:00', 0),
                                          (TIMESTAMP '2025-08-20 18:00:00', 0),
                                          (TIMESTAMP '2025-08-20 19:00:00', 0),
                                          (TIMESTAMP '2025-08-20 20:00:00', 0),
                                          (TIMESTAMP '2025-08-21 08:00:00', 0),
                                          (TIMESTAMP '2025-08-21 09:00:00', 0),
                                          (TIMESTAMP '2025-08-21 10:00:00', 0),
                                          (TIMESTAMP '2025-08-21 11:00:00', 0),
                                          (TIMESTAMP '2025-08-21 12:00:00', 0),
                                          (TIMESTAMP '2025-08-21 13:00:00', 0),
                                          (TIMESTAMP '2025-08-21 14:00:00', 0),
                                          (TIMESTAMP '2025-08-21 15:00:00', 0),
                                          (TIMESTAMP '2025-08-21 16:00:00', 0),
                                          (TIMESTAMP '2025-08-21 17:00:00', 0),
                                          (TIMESTAMP '2025-08-21 18:00:00', 0),
                                          (TIMESTAMP '2025-08-21 19:00:00', 0),
                                          (TIMESTAMP '2025-08-21 20:00:00', 0),
                                          (TIMESTAMP '2025-08-22 08:00:00', 0),
                                          (TIMESTAMP '2025-08-22 09:00:00', 0),
                                          (TIMESTAMP '2025-08-22 10:00:00', 0),
                                          (TIMESTAMP '2025-08-22 11:00:00', 0),
                                          (TIMESTAMP '2025-08-22 12:00:00', 0),
                                          (TIMESTAMP '2025-08-22 13:00:00', 0),
                                          (TIMESTAMP '2025-08-22 14:00:00', 0),
                                          (TIMESTAMP '2025-08-22 15:00:00', 0),
                                          (TIMESTAMP '2025-08-22 16:00:00', 0),
                                          (TIMESTAMP '2025-08-22 17:00:00', 0),
                                          (TIMESTAMP '2025-08-22 18:00:00', 0),
                                          (TIMESTAMP '2025-08-22 19:00:00', 0),
                                          (TIMESTAMP '2025-08-22 20:00:00', 0),
                                          (TIMESTAMP '2025-08-23 08:00:00', 0),
                                          (TIMESTAMP '2025-08-23 09:00:00', 0),
                                          (TIMESTAMP '2025-08-23 10:00:00', 0),
                                          (TIMESTAMP '2025-08-23 11:00:00', 0),
                                          (TIMESTAMP '2025-08-23 12:00:00', 0),
                                          (TIMESTAMP '2025-08-23 13:00:00', 0),
                                          (TIMESTAMP '2025-08-23 14:00:00', 0),
                                          (TIMESTAMP '2025-08-23 15:00:00', 0),
                                          (TIMESTAMP '2025-08-23 16:00:00', 0),
                                          (TIMESTAMP '2025-08-23 17:00:00', 0),
                                          (TIMESTAMP '2025-08-23 18:00:00', 0),
                                          (TIMESTAMP '2025-08-23 19:00:00', 0),
                                          (TIMESTAMP '2025-08-23 20:00:00', 0),
                                          (TIMESTAMP '2025-08-24 08:00:00', 0),
                                          (TIMESTAMP '2025-08-24 09:00:00', 0),
                                          (TIMESTAMP '2025-08-24 10:00:00', 0),
                                          (TIMESTAMP '2025-08-24 11:00:00', 0),
                                          (TIMESTAMP '2025-08-24 12:00:00', 0),
                                          (TIMESTAMP '2025-08-24 13:00:00', 0),
                                          (TIMESTAMP '2025-08-24 14:00:00', 0),
                                          (TIMESTAMP '2025-08-24 15:00:00', 0),
                                          (TIMESTAMP '2025-08-24 16:00:00', 0),
                                          (TIMESTAMP '2025-08-24 17:00:00', 0),
                                          (TIMESTAMP '2025-08-24 18:00:00', 0),
                                          (TIMESTAMP '2025-08-24 19:00:00', 0),
                                          (TIMESTAMP '2025-08-24 20:00:00', 0),
                                          (TIMESTAMP '2025-08-25 08:00:00', 0),
                                          (TIMESTAMP '2025-08-25 09:00:00', 0),
                                          (TIMESTAMP '2025-08-25 10:00:00', 0),
                                          (TIMESTAMP '2025-08-25 11:00:00', 0),
                                          (TIMESTAMP '2025-08-25 12:00:00', 0),
                                          (TIMESTAMP '2025-08-25 13:00:00', 0),
                                          (TIMESTAMP '2025-08-25 14:00:00', 0),
                                          (TIMESTAMP '2025-08-25 15:00:00', 0),
                                          (TIMESTAMP '2025-08-25 16:00:00', 0),
                                          (TIMESTAMP '2025-08-25 17:00:00', 0),
                                          (TIMESTAMP '2025-08-25 18:00:00', 0),
                                          (TIMESTAMP '2025-08-25 19:00:00', 0),
                                          (TIMESTAMP '2025-08-25 20:00:00', 0),
                                          (TIMESTAMP '2025-08-26 08:00:00', 0),
                                          (TIMESTAMP '2025-08-26 09:00:00', 0),
                                          (TIMESTAMP '2025-08-26 10:00:00', 0);
INSERT INTO "PUBLIC"."HALL_OCCUPANCY" VALUES
                                          (TIMESTAMP '2025-08-26 11:00:00', 0),
                                          (TIMESTAMP '2025-08-26 12:00:00', 0),
                                          (TIMESTAMP '2025-08-26 13:00:00', 0),
                                          (TIMESTAMP '2025-08-26 14:00:00', 0),
                                          (TIMESTAMP '2025-08-26 15:00:00', 0),
                                          (TIMESTAMP '2025-08-26 16:00:00', 0),
                                          (TIMESTAMP '2025-08-26 17:00:00', 0),
                                          (TIMESTAMP '2025-08-26 18:00:00', 0),
                                          (TIMESTAMP '2025-08-26 19:00:00', 0),
                                          (TIMESTAMP '2025-08-26 20:00:00', 0),
                                          (TIMESTAMP '2025-08-27 08:00:00', 0),
                                          (TIMESTAMP '2025-08-27 09:00:00', 0),
                                          (TIMESTAMP '2025-08-27 10:00:00', 0),
                                          (TIMESTAMP '2025-08-27 11:00:00', 0),
                                          (TIMESTAMP '2025-08-27 12:00:00', 0),
                                          (TIMESTAMP '2025-08-27 13:00:00', 0),
                                          (TIMESTAMP '2025-08-27 14:00:00', 0),
                                          (TIMESTAMP '2025-08-27 15:00:00', 0),
                                          (TIMESTAMP '2025-08-27 16:00:00', 0),
                                          (TIMESTAMP '2025-08-27 17:00:00', 0),
                                          (TIMESTAMP '2025-08-27 18:00:00', 0),
                                          (TIMESTAMP '2025-08-27 19:00:00', 0),
                                          (TIMESTAMP '2025-08-27 20:00:00', 0),
                                          (TIMESTAMP '2025-08-28 08:00:00', 0),
                                          (TIMESTAMP '2025-08-28 09:00:00', 0),
                                          (TIMESTAMP '2025-08-28 10:00:00', 0),
                                          (TIMESTAMP '2025-08-28 11:00:00', 0),
                                          (TIMESTAMP '2025-08-28 12:00:00', 0),
                                          (TIMESTAMP '2025-08-28 13:00:00', 0),
                                          (TIMESTAMP '2025-08-28 14:00:00', 0),
                                          (TIMESTAMP '2025-08-28 15:00:00', 0),
                                          (TIMESTAMP '2025-08-28 16:00:00', 0),
                                          (TIMESTAMP '2025-08-28 17:00:00', 0),
                                          (TIMESTAMP '2025-08-28 18:00:00', 0),
                                          (TIMESTAMP '2025-08-28 19:00:00', 0),
                                          (TIMESTAMP '2025-08-28 20:00:00', 0),
                                          (TIMESTAMP '2025-08-29 08:00:00', 0),
                                          (TIMESTAMP '2025-08-29 09:00:00', 0),
                                          (TIMESTAMP '2025-08-29 10:00:00', 0),
                                          (TIMESTAMP '2025-08-29 11:00:00', 0),
                                          (TIMESTAMP '2025-08-29 12:00:00', 0),
                                          (TIMESTAMP '2025-08-29 13:00:00', 0),
                                          (TIMESTAMP '2025-08-29 14:00:00', 0),
                                          (TIMESTAMP '2025-08-29 15:00:00', 0),
                                          (TIMESTAMP '2025-08-29 16:00:00', 0),
                                          (TIMESTAMP '2025-08-29 17:00:00', 0),
                                          (TIMESTAMP '2025-08-29 18:00:00', 0),
                                          (TIMESTAMP '2025-08-29 19:00:00', 0),
                                          (TIMESTAMP '2025-08-29 20:00:00', 0),
                                          (TIMESTAMP '2025-08-30 08:00:00', 0),
                                          (TIMESTAMP '2025-08-30 09:00:00', 0),
                                          (TIMESTAMP '2025-08-30 10:00:00', 0),
                                          (TIMESTAMP '2025-08-30 11:00:00', 0),
                                          (TIMESTAMP '2025-08-30 12:00:00', 0),
                                          (TIMESTAMP '2025-08-30 13:00:00', 0),
                                          (TIMESTAMP '2025-08-30 14:00:00', 0),
                                          (TIMESTAMP '2025-08-30 15:00:00', 0),
                                          (TIMESTAMP '2025-08-30 16:00:00', 0),
                                          (TIMESTAMP '2025-08-30 17:00:00', 0),
                                          (TIMESTAMP '2025-08-30 18:00:00', 0),
                                          (TIMESTAMP '2025-08-30 19:00:00', 0),
                                          (TIMESTAMP '2025-08-30 20:00:00', 0),
                                          (TIMESTAMP '2025-08-31 08:00:00', 0),
                                          (TIMESTAMP '2025-08-31 09:00:00', 0),
                                          (TIMESTAMP '2025-08-31 10:00:00', 0),
                                          (TIMESTAMP '2025-08-31 11:00:00', 0),
                                          (TIMESTAMP '2025-08-31 12:00:00', 0),
                                          (TIMESTAMP '2025-08-31 13:00:00', 0),
                                          (TIMESTAMP '2025-08-31 14:00:00', 0),
                                          (TIMESTAMP '2025-08-31 15:00:00', 0),
                                          (TIMESTAMP '2025-08-31 16:00:00', 0),
                                          (TIMESTAMP '2025-08-31 17:00:00', 0),
                                          (TIMESTAMP '2025-08-31 18:00:00', 0),
                                          (TIMESTAMP '2025-08-31 19:00:00', 0),
                                          (TIMESTAMP '2025-08-31 20:00:00', 0),
                                          (TIMESTAMP '2025-09-01 08:00:00', 0),
                                          (TIMESTAMP '2025-09-01 09:00:00', 0),
                                          (TIMESTAMP '2025-09-01 10:00:00', 0),
                                          (TIMESTAMP '2025-09-01 11:00:00', 0),
                                          (TIMESTAMP '2025-09-01 12:00:00', 0),
                                          (TIMESTAMP '2025-09-01 13:00:00', 0),
                                          (TIMESTAMP '2025-09-01 14:00:00', 0),
                                          (TIMESTAMP '2025-09-01 15:00:00', 0),
                                          (TIMESTAMP '2025-09-01 16:00:00', 0),
                                          (TIMESTAMP '2025-09-01 17:00:00', 0),
                                          (TIMESTAMP '2025-09-01 18:00:00', 0),
                                          (TIMESTAMP '2025-09-01 19:00:00', 0),
                                          (TIMESTAMP '2025-09-01 20:00:00', 0),
                                          (TIMESTAMP '2025-09-02 08:00:00', 0),
                                          (TIMESTAMP '2025-09-02 09:00:00', 0),
                                          (TIMESTAMP '2025-09-02 10:00:00', 0),
                                          (TIMESTAMP '2025-09-02 11:00:00', 0),
                                          (TIMESTAMP '2025-09-02 12:00:00', 0),
                                          (TIMESTAMP '2025-09-02 13:00:00', 0),
                                          (TIMESTAMP '2025-09-02 14:00:00', 0),
                                          (TIMESTAMP '2025-09-02 15:00:00', 0),
                                          (TIMESTAMP '2025-09-02 16:00:00', 0),
                                          (TIMESTAMP '2025-09-02 17:00:00', 0),
                                          (TIMESTAMP '2025-09-02 18:00:00', 0),
                                          (TIMESTAMP '2025-09-02 19:00:00', 0),
                                          (TIMESTAMP '2025-09-02 20:00:00', 0),
                                          (TIMESTAMP '2025-09-03 08:00:00', 0),
                                          (TIMESTAMP '2025-09-03 09:00:00', 0),
                                          (TIMESTAMP '2025-09-03 10:00:00', 0),
                                          (TIMESTAMP '2025-09-03 11:00:00', 0),
                                          (TIMESTAMP '2025-09-03 12:00:00', 0),
                                          (TIMESTAMP '2025-09-03 13:00:00', 0);
INSERT INTO "PUBLIC"."HALL_OCCUPANCY" VALUES
                                          (TIMESTAMP '2025-09-03 14:00:00', 0),
                                          (TIMESTAMP '2025-09-03 15:00:00', 0),
                                          (TIMESTAMP '2025-09-03 16:00:00', 0),
                                          (TIMESTAMP '2025-09-03 17:00:00', 0),
                                          (TIMESTAMP '2025-09-03 18:00:00', 0),
                                          (TIMESTAMP '2025-09-03 19:00:00', 0),
                                          (TIMESTAMP '2025-09-03 20:00:00', 0),
                                          (TIMESTAMP '2025-09-04 08:00:00', 0),
                                          (TIMESTAMP '2025-09-04 09:00:00', 0),
                                          (TIMESTAMP '2025-09-04 10:00:00', 0),
                                          (TIMESTAMP '2025-09-04 11:00:00', 0),
                                          (TIMESTAMP '2025-09-04 12:00:00', 0),
                                          (TIMESTAMP '2025-09-04 13:00:00', 0),
                                          (TIMESTAMP '2025-09-04 14:00:00', 0),
                                          (TIMESTAMP '2025-09-04 15:00:00', 0),
                                          (TIMESTAMP '2025-09-04 16:00:00', 0),
                                          (TIMESTAMP '2025-09-04 17:00:00', 0),
                                          (TIMESTAMP '2025-09-04 18:00:00', 0),
                                          (TIMESTAMP '2025-09-04 19:00:00', 0),
                                          (TIMESTAMP '2025-09-04 20:00:00', 0),
                                          (TIMESTAMP '2025-09-05 08:00:00', 0),
                                          (TIMESTAMP '2025-09-05 09:00:00', 0),
                                          (TIMESTAMP '2025-09-05 10:00:00', 0),
                                          (TIMESTAMP '2025-09-05 11:00:00', 0),
                                          (TIMESTAMP '2025-09-05 12:00:00', 0),
                                          (TIMESTAMP '2025-09-05 13:00:00', 0),
                                          (TIMESTAMP '2025-09-05 14:00:00', 0),
                                          (TIMESTAMP '2025-09-05 15:00:00', 0),
                                          (TIMESTAMP '2025-09-05 16:00:00', 0),
                                          (TIMESTAMP '2025-09-05 17:00:00', 0),
                                          (TIMESTAMP '2025-09-05 18:00:00', 0),
                                          (TIMESTAMP '2025-09-05 19:00:00', 0),
                                          (TIMESTAMP '2025-09-05 20:00:00', 0),
                                          (TIMESTAMP '2025-09-06 08:00:00', 0),
                                          (TIMESTAMP '2025-09-06 09:00:00', 0),
                                          (TIMESTAMP '2025-09-06 10:00:00', 0),
                                          (TIMESTAMP '2025-09-06 11:00:00', 0),
                                          (TIMESTAMP '2025-09-06 12:00:00', 0),
                                          (TIMESTAMP '2025-09-06 13:00:00', 0),
                                          (TIMESTAMP '2025-09-06 14:00:00', 0),
                                          (TIMESTAMP '2025-09-06 15:00:00', 0),
                                          (TIMESTAMP '2025-09-06 16:00:00', 0),
                                          (TIMESTAMP '2025-09-06 17:00:00', 0),
                                          (TIMESTAMP '2025-09-06 18:00:00', 0),
                                          (TIMESTAMP '2025-09-06 19:00:00', 0),
                                          (TIMESTAMP '2025-09-06 20:00:00', 0),
                                          (TIMESTAMP '2025-09-07 08:00:00', 0),
                                          (TIMESTAMP '2025-09-07 09:00:00', 0),
                                          (TIMESTAMP '2025-09-07 10:00:00', 0),
                                          (TIMESTAMP '2025-09-07 11:00:00', 0),
                                          (TIMESTAMP '2025-09-07 12:00:00', 0),
                                          (TIMESTAMP '2025-09-07 13:00:00', 0),
                                          (TIMESTAMP '2025-09-07 14:00:00', 0),
                                          (TIMESTAMP '2025-09-07 15:00:00', 0),
                                          (TIMESTAMP '2025-09-07 16:00:00', 0),
                                          (TIMESTAMP '2025-09-07 17:00:00', 0),
                                          (TIMESTAMP '2025-09-07 18:00:00', 0),
                                          (TIMESTAMP '2025-09-07 19:00:00', 0),
                                          (TIMESTAMP '2025-09-07 20:00:00', 0),
                                          (TIMESTAMP '2025-09-08 08:00:00', 0),
                                          (TIMESTAMP '2025-09-08 09:00:00', 0),
                                          (TIMESTAMP '2025-09-08 10:00:00', 0),
                                          (TIMESTAMP '2025-09-08 11:00:00', 0),
                                          (TIMESTAMP '2025-09-08 12:00:00', 0),
                                          (TIMESTAMP '2025-09-08 13:00:00', 0),
                                          (TIMESTAMP '2025-09-08 14:00:00', 0),
                                          (TIMESTAMP '2025-09-08 15:00:00', 0),
                                          (TIMESTAMP '2025-09-08 16:00:00', 0),
                                          (TIMESTAMP '2025-09-08 17:00:00', 0),
                                          (TIMESTAMP '2025-09-08 18:00:00', 0),
                                          (TIMESTAMP '2025-09-08 19:00:00', 0),
                                          (TIMESTAMP '2025-09-08 20:00:00', 0),
                                          (TIMESTAMP '2025-09-09 08:00:00', 0),
                                          (TIMESTAMP '2025-09-09 09:00:00', 0),
                                          (TIMESTAMP '2025-09-09 10:00:00', 0),
                                          (TIMESTAMP '2025-09-09 11:00:00', 0),
                                          (TIMESTAMP '2025-09-09 12:00:00', 0),
                                          (TIMESTAMP '2025-09-09 13:00:00', 0),
                                          (TIMESTAMP '2025-09-09 14:00:00', 0),
                                          (TIMESTAMP '2025-09-09 15:00:00', 0),
                                          (TIMESTAMP '2025-09-09 16:00:00', 0),
                                          (TIMESTAMP '2025-09-09 17:00:00', 0),
                                          (TIMESTAMP '2025-09-09 18:00:00', 0),
                                          (TIMESTAMP '2025-09-09 19:00:00', 0),
                                          (TIMESTAMP '2025-09-09 20:00:00', 0),
                                          (TIMESTAMP '2025-09-10 08:00:00', 0),
                                          (TIMESTAMP '2025-09-10 09:00:00', 0),
                                          (TIMESTAMP '2025-09-10 10:00:00', 0),
                                          (TIMESTAMP '2025-09-10 11:00:00', 0),
                                          (TIMESTAMP '2025-09-10 12:00:00', 0),
                                          (TIMESTAMP '2025-09-10 13:00:00', 0),
                                          (TIMESTAMP '2025-09-10 14:00:00', 0),
                                          (TIMESTAMP '2025-09-10 15:00:00', 0),
                                          (TIMESTAMP '2025-09-10 16:00:00', 0),
                                          (TIMESTAMP '2025-09-10 17:00:00', 0),
                                          (TIMESTAMP '2025-09-10 18:00:00', 0),
                                          (TIMESTAMP '2025-09-10 19:00:00', 0),
                                          (TIMESTAMP '2025-09-10 20:00:00', 0),
                                          (TIMESTAMP '2025-09-11 08:00:00', 0),
                                          (TIMESTAMP '2025-09-11 09:00:00', 0),
                                          (TIMESTAMP '2025-09-11 10:00:00', 0),
                                          (TIMESTAMP '2025-09-11 11:00:00', 0),
                                          (TIMESTAMP '2025-09-11 12:00:00', 0),
                                          (TIMESTAMP '2025-09-11 13:00:00', 0),
                                          (TIMESTAMP '2025-09-11 14:00:00', 0),
                                          (TIMESTAMP '2025-09-11 15:00:00', 0),
                                          (TIMESTAMP '2025-09-11 16:00:00', 0);
INSERT INTO "PUBLIC"."HALL_OCCUPANCY" VALUES
                                          (TIMESTAMP '2025-09-11 17:00:00', 0),
                                          (TIMESTAMP '2025-09-11 18:00:00', 0),
                                          (TIMESTAMP '2025-09-11 19:00:00', 0),
                                          (TIMESTAMP '2025-09-11 20:00:00', 0),
                                          (TIMESTAMP '2025-09-12 08:00:00', 0),
                                          (TIMESTAMP '2025-09-12 09:00:00', 0),
                                          (TIMESTAMP '2025-09-12 10:00:00', 0),
                                          (TIMESTAMP '2025-09-12 11:00:00', 0),
                                          (TIMESTAMP '2025-09-12 12:00:00', 0),
                                          (TIMESTAMP '2025-09-12 13:00:00', 0),
                                          (TIMESTAMP '2025-09-12 14:00:00', 0),
                                          (TIMESTAMP '2025-09-12 15:00:00', 0),
                                          (TIMESTAMP '2025-09-12 16:00:00', 0),
                                          (TIMESTAMP '2025-09-12 17:00:00', 0),
                                          (TIMESTAMP '2025-09-12 18:00:00', 0),
                                          (TIMESTAMP '2025-09-12 19:00:00', 0),
                                          (TIMESTAMP '2025-09-12 20:00:00', 0),
                                          (TIMESTAMP '2025-09-13 08:00:00', 0),
                                          (TIMESTAMP '2025-09-13 09:00:00', 0),
                                          (TIMESTAMP '2025-09-13 10:00:00', 0),
                                          (TIMESTAMP '2025-09-13 11:00:00', 0),
                                          (TIMESTAMP '2025-09-13 12:00:00', 0),
                                          (TIMESTAMP '2025-09-13 13:00:00', 0),
                                          (TIMESTAMP '2025-09-13 14:00:00', 0),
                                          (TIMESTAMP '2025-09-13 15:00:00', 0),
                                          (TIMESTAMP '2025-09-13 16:00:00', 0),
                                          (TIMESTAMP '2025-09-13 17:00:00', 0),
                                          (TIMESTAMP '2025-09-13 18:00:00', 0),
                                          (TIMESTAMP '2025-09-13 19:00:00', 0),
                                          (TIMESTAMP '2025-09-13 20:00:00', 0),
                                          (TIMESTAMP '2025-09-14 08:00:00', 0),
                                          (TIMESTAMP '2025-09-14 09:00:00', 0),
                                          (TIMESTAMP '2025-09-14 10:00:00', 0),
                                          (TIMESTAMP '2025-09-14 11:00:00', 0),
                                          (TIMESTAMP '2025-09-14 12:00:00', 0),
                                          (TIMESTAMP '2025-09-14 13:00:00', 0),
                                          (TIMESTAMP '2025-09-14 14:00:00', 0),
                                          (TIMESTAMP '2025-09-14 15:00:00', 0),
                                          (TIMESTAMP '2025-09-14 16:00:00', 0),
                                          (TIMESTAMP '2025-09-14 17:00:00', 0),
                                          (TIMESTAMP '2025-09-14 18:00:00', 0),
                                          (TIMESTAMP '2025-09-14 19:00:00', 0),
                                          (TIMESTAMP '2025-09-14 20:00:00', 0),
                                          (TIMESTAMP '2025-09-15 08:00:00', 0),
                                          (TIMESTAMP '2025-09-15 09:00:00', 0),
                                          (TIMESTAMP '2025-09-15 10:00:00', 0),
                                          (TIMESTAMP '2025-09-15 11:00:00', 0),
                                          (TIMESTAMP '2025-09-15 12:00:00', 0),
                                          (TIMESTAMP '2025-09-15 13:00:00', 0),
                                          (TIMESTAMP '2025-09-15 14:00:00', 0),
                                          (TIMESTAMP '2025-09-15 15:00:00', 0),
                                          (TIMESTAMP '2025-09-15 16:00:00', 0),
                                          (TIMESTAMP '2025-09-15 17:00:00', 0),
                                          (TIMESTAMP '2025-09-15 18:00:00', 0),
                                          (TIMESTAMP '2025-09-15 19:00:00', 0),
                                          (TIMESTAMP '2025-09-15 20:00:00', 0),
                                          (TIMESTAMP '2025-09-16 08:00:00', 0),
                                          (TIMESTAMP '2025-09-16 09:00:00', 0),
                                          (TIMESTAMP '2025-09-16 10:00:00', 0),
                                          (TIMESTAMP '2025-09-16 11:00:00', 0),
                                          (TIMESTAMP '2025-09-16 12:00:00', 0),
                                          (TIMESTAMP '2025-09-16 13:00:00', 0),
                                          (TIMESTAMP '2025-09-16 14:00:00', 0),
                                          (TIMESTAMP '2025-09-16 15:00:00', 0),
                                          (TIMESTAMP '2025-09-16 16:00:00', 0),
                                          (TIMESTAMP '2025-09-16 17:00:00', 0),
                                          (TIMESTAMP '2025-09-16 18:00:00', 0),
                                          (TIMESTAMP '2025-09-16 19:00:00', 0),
                                          (TIMESTAMP '2025-09-16 20:00:00', 0),
                                          (TIMESTAMP '2025-09-17 08:00:00', 0),
                                          (TIMESTAMP '2025-09-17 09:00:00', 0),
                                          (TIMESTAMP '2025-09-17 10:00:00', 0),
                                          (TIMESTAMP '2025-09-17 11:00:00', 0),
                                          (TIMESTAMP '2025-09-17 12:00:00', 0),
                                          (TIMESTAMP '2025-09-17 13:00:00', 0),
                                          (TIMESTAMP '2025-09-17 14:00:00', 0),
                                          (TIMESTAMP '2025-09-17 15:00:00', 0),
                                          (TIMESTAMP '2025-09-17 16:00:00', 0),
                                          (TIMESTAMP '2025-09-17 17:00:00', 0),
                                          (TIMESTAMP '2025-09-17 18:00:00', 0),
                                          (TIMESTAMP '2025-09-17 19:00:00', 0),
                                          (TIMESTAMP '2025-09-17 20:00:00', 0);
CREATE CACHED TABLE "PUBLIC"."NEWS"(
    "ID" UUID NOT NULL,
    "CREATED_AT" TIMESTAMP(6) NOT NULL,
    "DESCRIPTION" CHARACTER VARYING NOT NULL,
    "TITLE" CHARACTER VARYING(255) NOT NULL
);
ALTER TABLE "PUBLIC"."NEWS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_2" PRIMARY KEY("ID");
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.NEWS;
INSERT INTO "PUBLIC"."NEWS" VALUES
                                (UUID '7a9426f7-74e8-4b88-bbc9-2483c9341d80', TIMESTAMP '2025-04-04 10:00:00', U&'\0418\043b\043e\043d \041c\0430\0441\043a \043f\043e\0441\0435\0442\0438\043b \043a\043e\0432\043e\0440\043a\0438\043d\0433 \0438 \0437\0430\043f\0443\0441\0442\0438\043b \0441\043b\0443\0436\0431\0443 \0442\0430\043a\0441\0438 \0434\043b\044f \0434\043e\0441\0442\0430\0432\043a\0438 \0438\0437 \043b\044e\0431\043e\0439 \0442\043e\0447\043a\0438 \0421\0430\043d\043a\0442-\041f\0435\0442\0435\0440\0431\0443\0440\0433\0430.', 'Space X'),
                                (UUID '422f1a96-49c5-4d2e-9477-610811c84390', TIMESTAMP '2025-04-03 08:15:00', U&'10 \0430\043f\0440\0435\043b\044f \0441 2:00 \0434\043e 4:00 \043d\0430 \043d\0430\0448\0435\0439 \043f\043b\0430\0442\0444\043e\0440\043c\0435 \043f\0440\043e\0439\0434\0443\0442 \043f\043b\0430\043d\043e\0432\044b\0435 \0442\0435\0445\043d\0438\0447\0435\0441\043a\0438\0435 \0440\0430\0431\043e\0442\044b. \041d\0435\043a\043e\0442\043e\0440\044b\0435 \0441\0435\0440\0432\0438\0441\044b \043c\043e\0433\0443\0442 \0431\044b\0442\044c \0432\0440\0435\043c\0435\043d\043d\043e \043d\0435\0434\043e\0441\0442\0443\043f\043d\044b.', U&'\041f\043b\0430\043d\043e\0432\043e\0435 \043e\0431\0441\043b\0443\0436\0438\0432\0430\043d\0438\0435 \0441\0438\0441\0442\0435\043c\044b');
CREATE CACHED TABLE "PUBLIC"."NEWS_KEYS"(
    "NEWS_ID" UUID NOT NULL,
    "KEYS" CHARACTER VARYING(255) NOT NULL
);
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.NEWS_KEYS;
INSERT INTO "PUBLIC"."NEWS_KEYS" VALUES
                                     (UUID '7a9426f7-74e8-4b88-bbc9-2483c9341d80', 'arch.png'),
                                     (UUID '422f1a96-49c5-4d2e-9477-610811c84390', 'arch.png');
CREATE CACHED TABLE "PUBLIC"."NEWS_TAGS"(
    "NEWS_ID" UUID NOT NULL,
    "TAG" ENUM('ART', 'BUSINESS', 'IT', 'MARKETING', 'PSYCHOLOGY', 'SCIENCE', 'SUCCESS_STORY', 'TECHNOLOGY')
);
-- 4 +/- SELECT COUNT(*) FROM PUBLIC.NEWS_TAGS;
INSERT INTO "PUBLIC"."NEWS_TAGS" VALUES
                                     (UUID '7a9426f7-74e8-4b88-bbc9-2483c9341d80', 'TECHNOLOGY'),
                                     (UUID '7a9426f7-74e8-4b88-bbc9-2483c9341d80', 'IT'),
                                     (UUID '422f1a96-49c5-4d2e-9477-610811c84390', 'TECHNOLOGY'),
                                     (UUID '422f1a96-49c5-4d2e-9477-610811c84390', 'SCIENCE');
CREATE CACHED TABLE "PUBLIC"."REVIEWS"(
    "ID" UUID NOT NULL,
    "COMMENT" CHARACTER VARYING(255),
    "CREATED_AT" TIMESTAMP(6) NOT NULL,
    "RATING" TINYINT NOT NULL,
    "USER_ID" UUID NOT NULL
);
ALTER TABLE "PUBLIC"."REVIEWS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_6" PRIMARY KEY("ID");
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.REVIEWS;
CREATE CACHED TABLE "PUBLIC"."SCHEDULE"(
    "DAY_OFF" DATE NOT NULL,
    "DESCRIPTION" CHARACTER VARYING(255),
    "START_TIME" TIME(6),
    "STOP_TIME" TIME(6),
    "TAG" ENUM('ELECTRICITY_OUTAGE', 'HOLIDAY', 'PRIVATE_EVENT', 'QUARANTINE', 'SANITARY_DAY', 'TECHNICAL_WORK', 'UNDEFINED_REASON', 'WEATHER_EMERGENCY', 'WEEKEND') NOT NULL
);
ALTER TABLE "PUBLIC"."SCHEDULE" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_5" PRIMARY KEY("DAY_OFF");
-- 4 +/- SELECT COUNT(*) FROM PUBLIC.SCHEDULE;
INSERT INTO "PUBLIC"."SCHEDULE" VALUES
                                    (DATE '2025-08-23', U&'\0412\044b\0445\043e\0434\043d\043e\0439 \0434\0435\043d\044c', NULL, NULL, 'WEEKEND'),
                                    (DATE '2025-08-30', U&'\0412\044b\0445\043e\0434\043d\043e\0439 \0434\0435\043d\044c', NULL, NULL, 'WEEKEND'),
                                    (DATE '2025-09-06', U&'\0412\044b\0445\043e\0434\043d\043e\0439 \0434\0435\043d\044c', NULL, NULL, 'WEEKEND'),
                                    (DATE '2025-09-13', U&'\0412\044b\0445\043e\0434\043d\043e\0439 \0434\0435\043d\044c', NULL, NULL, 'WEEKEND');
CREATE CACHED TABLE "PUBLIC"."TICKETS"(
    "ID" UUID NOT NULL,
    "CLOSED_AT" TIMESTAMP(6),
    "CREATED_AT" TIMESTAMP(6) NOT NULL,
    "DESCRIPTION" CHARACTER VARYING NOT NULL,
    "FIRST_RESPONDED_AT" TIMESTAMP(6),
    "PRIORITY" ENUM('CRITICAL', 'DEFAULT', 'HIGH', 'LOW', 'MEDIUM') NOT NULL,
    "REASON" CHARACTER VARYING(255),
    "RESOLVED_AT" TIMESTAMP(6),
    "STATUS" ENUM('CLOSED', 'IN_PROGRESS', 'ON_HOLD', 'OPEN', 'REJECTED', 'RESOLVED') NOT NULL,
    "TYPE" TINYINT NOT NULL,
    "UPDATED_AT" TIMESTAMP(6),
    "AREA_ID" UUID NOT NULL,
    "USER_ID" UUID NOT NULL
);
ALTER TABLE "PUBLIC"."TICKETS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_DBF" PRIMARY KEY("ID");
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.TICKETS;
CREATE CACHED TABLE "PUBLIC"."USER_ROLES"(
    "USER_ID" UUID NOT NULL,
    "ROLES" ENUM('ROLE_ADMIN', 'ROLE_SUPERADMIN', 'ROLE_USER') NOT NULL
);
ALTER TABLE "PUBLIC"."USER_ROLES" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_C" PRIMARY KEY("USER_ID", "ROLES");
-- 4 +/- SELECT COUNT(*) FROM PUBLIC.USER_ROLES;
INSERT INTO "PUBLIC"."USER_ROLES" VALUES
                                      (UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7', 'ROLE_USER'),
                                      (UUID '3c26f460-2177-456f-92fc-c284c91a711d', 'ROLE_USER'),
                                      (UUID '66e2ee9e-72c0-40ea-b8bf-0bd1eae89d67', 'ROLE_USER'),
                                      (UUID '7f313358-746a-4bf2-83fa-28cd229e49fe', 'ROLE_USER');
CREATE CACHED TABLE "PUBLIC"."USERS"(
    "ID" UUID NOT NULL,
    "CREATED_AT" TIMESTAMP(6) NOT NULL,
    "EMAIL" CHARACTER VARYING(255),
    "FIRST_NAME" CHARACTER VARYING(255) NOT NULL,
    "LAST_NAME" CHARACTER VARYING(255),
    "PASSWORD_HASH" CHARACTER VARYING(255) NOT NULL,
    "PHONE" CHARACTER VARYING(255),
    "PHOTO_URL" CHARACTER VARYING(255),
    "STATUS" ENUM('BANNED', 'CREATED', 'DELETED', 'VERIFIED') NOT NULL,
    "SUBSCRIBED_TO_NOTIFICATIONS" BOOLEAN DEFAULT TRUE NOT NULL,
    "TG_ID" BIGINT,
    "UPDATED_AT" TIMESTAMP(6),
    "USERNAME" CHARACTER VARYING(255) NOT NULL
);
ALTER TABLE "PUBLIC"."USERS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_4D" PRIMARY KEY("ID");
-- 4 +/- SELECT COUNT(*) FROM PUBLIC.USERS;
INSERT INTO "PUBLIC"."USERS" VALUES
                                 (UUID '763e08db-7f0b-4e0e-bfb5-5c77404211f7', TIMESTAMP '2025-04-03 12:00:00', 'alice@example.com', 'Alice', 'Johnson', '$2b$12$abcdefghijklmnopqrstuv', '+79123456789', NULL, 'CREATED', FALSE, 1234567890, NULL, 'alicejohnson'),
                                 (UUID '3c26f460-2177-456f-92fc-c284c91a711d', TIMESTAMP '2025-04-03 12:05:00', 'bob@example.com', 'Bob', 'Smith', '$2b$12$zyxwvutsrqponmlkjihgfedc', '+79219876543', NULL, 'CREATED', FALSE, 1987654321, NULL, 'bobsmith'),
                                 (UUID '66e2ee9e-72c0-40ea-b8bf-0bd1eae89d67', TIMESTAMP '2025-04-03 12:10:00', 'charlie@example.com', 'Charlie Davis', 'Davis', '$2b$12$1234567890abcdefgijklmn', '+79219876542', NULL, 'BANNED', FALSE, 8987654325, NULL, 'charliedavis'),
                                 (UUID '7f313358-746a-4bf2-83fa-28cd229e49fe', TIMESTAMP '2025-08-18 23:09:25.510123', 'kogav74753@efpaper.com', 'Ivan', 'Ivanov', '$2a$10$t4oFEtejs5c17vbgKz8QqeMHUTQd9VVADlmFpXcfzMioAhn9QqauG', '88005553535', 'https://t.me/i/userpic/320/ivanov_ivan.jpg', 'VERIFIED', TRUE, 123458999, TIMESTAMP '2025-08-18 23:09:28.044649', 'ivanov_ietoto;tg_id_123458999');
ALTER TABLE "PUBLIC"."TICKETS" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_DB" CHECK("TYPE" BETWEEN 0 AND 3) NOCHECK;
ALTER TABLE "PUBLIC"."USERS" ADD CONSTRAINT "PUBLIC"."UK6DOTKOTT2KJSP8VW4D0M25FB7" UNIQUE NULLS DISTINCT ("EMAIL");
ALTER TABLE "PUBLIC"."USERS" ADD CONSTRAINT "PUBLIC"."UKR43AF9AP4EDM43MMTQ01ODDJ6" UNIQUE NULLS DISTINCT ("USERNAME");
ALTER TABLE "PUBLIC"."USERS" ADD CONSTRAINT "PUBLIC"."UKDU5V5SR43G5BFNJI4VB8HG5S3" UNIQUE NULLS DISTINCT ("PHONE");
ALTER TABLE "PUBLIC"."EVENTS" ADD CONSTRAINT "PUBLIC"."UKAITRT92BH0X5PVL10E071KO8Q" UNIQUE NULLS DISTINCT ("SYSTEM_BOOKING_ID");
ALTER TABLE "PUBLIC"."EVENT_TAGS" ADD CONSTRAINT "PUBLIC"."FKIWOYITW224YKOM58M5XNOA9Y6" FOREIGN KEY("EVENT_ID") REFERENCES "PUBLIC"."EVENTS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."BOOKINGS" ADD CONSTRAINT "PUBLIC"."FKEYOG2OIC85XG7HSU2JE2LX3S6" FOREIGN KEY("USER_ID") REFERENCES "PUBLIC"."USERS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENT_TIMES" ADD CONSTRAINT "PUBLIC"."FK8R9W1H5UHRTHHBLYGNA40VLW2" FOREIGN KEY("EVENT_ID") REFERENCES "PUBLIC"."EVENTS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENT_KEYS" ADD CONSTRAINT "PUBLIC"."FKHLNENO8JHNVW814AFS0L9I8MB" FOREIGN KEY("EVENT_ID") REFERENCES "PUBLIC"."EVENTS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."NEWS_KEYS" ADD CONSTRAINT "PUBLIC"."FK339M6AFSQ2RJAJRT16A0YH4S2" FOREIGN KEY("NEWS_ID") REFERENCES "PUBLIC"."NEWS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENT_PARTICIPATION_FORMATS" ADD CONSTRAINT "PUBLIC"."FKOCTVYVCFGE6CE023OISRX5SU9" FOREIGN KEY("EVENT_ID") REFERENCES "PUBLIC"."EVENTS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."REVIEWS" ADD CONSTRAINT "PUBLIC"."FKCGY7QJC1R99DP117Y9EN6LXYE" FOREIGN KEY("USER_ID") REFERENCES "PUBLIC"."USERS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENT_USERS" ADD CONSTRAINT "PUBLIC"."FKCI0B9YS3AWPOUR3LSN05DQ8R4" FOREIGN KEY("EVENT_ID") REFERENCES "PUBLIC"."EVENTS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."AREA_FEATURES" ADD CONSTRAINT "PUBLIC"."FKDLMNBH0HJ98OJ33QD04FONGBF" FOREIGN KEY("AREA_ID") REFERENCES "PUBLIC"."AREAS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."AREA_KEYS" ADD CONSTRAINT "PUBLIC"."FKDMIB6H2V5DM5FUEYU4E5KC5SE" FOREIGN KEY("AREA_ID") REFERENCES "PUBLIC"."AREAS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."BOOKINGS" ADD CONSTRAINT "PUBLIC"."FK27OIJOA8M2UWOR0MITY3VTSY6" FOREIGN KEY("AREA_ID") REFERENCES "PUBLIC"."AREAS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENTS" ADD CONSTRAINT "PUBLIC"."FK3Y74KSY8RNI02F0DSX6EHNP8W" FOREIGN KEY("AREA_ID") REFERENCES "PUBLIC"."AREAS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."NEWS_TAGS" ADD CONSTRAINT "PUBLIC"."FKI06SDGPSVQ2OXTHARQ5Q1RC3X" FOREIGN KEY("NEWS_ID") REFERENCES "PUBLIC"."NEWS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."TICKETS" ADD CONSTRAINT "PUBLIC"."FKSV8TOLHTKSV43G34BQ2GIJYNR" FOREIGN KEY("AREA_ID") REFERENCES "PUBLIC"."AREAS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."TICKETS" ADD CONSTRAINT "PUBLIC"."FK4EQSEBPIMNJEN0Q46JA6FL2HL" FOREIGN KEY("USER_ID") REFERENCES "PUBLIC"."USERS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."USER_ROLES" ADD CONSTRAINT "PUBLIC"."FKHFH9DX7W3UBF1CO1VDEV94G3F" FOREIGN KEY("USER_ID") REFERENCES "PUBLIC"."USERS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENT_USERS" ADD CONSTRAINT "PUBLIC"."FKL0JAR0MNL3HQK84WWIO9U8CMY" FOREIGN KEY("USER_ID") REFERENCES "PUBLIC"."USERS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENTS" ADD CONSTRAINT "PUBLIC"."FK52FHVWNWCFV7AC287RGH5WQYY" FOREIGN KEY("SYSTEM_BOOKING_ID") REFERENCES "PUBLIC"."BOOKINGS"("ID") NOCHECK;
ALTER TABLE "PUBLIC"."EVENT_FORMATS" ADD CONSTRAINT "PUBLIC"."FKOPC2WURGDL2B4JI0K1CGMC3RN" FOREIGN KEY("EVENT_ID") REFERENCES "PUBLIC"."EVENTS"("ID") NOCHECK;
