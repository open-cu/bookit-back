CREATE TABLE "Users"(
                        "id" BIGINT NOT NULL,
                        "tg_id" BIGINT NOT NULL,
                        "name" VARCHAR(255) NOT NULL,
                        "email" TEXT NOT NULL,
                        "password_hash" VARCHAR(255) NULL,
                        "phone" BIGINT NOT NULL,
                        "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
                        "status" VARCHAR(255) NOT NULL DEFAULT 'User'
);
ALTER TABLE
    "Users" ADD PRIMARY KEY("id");
ALTER TABLE
    "Users" ADD CONSTRAINT "users_tg_id_unique" UNIQUE("tg_id");
ALTER TABLE
    "Users" ADD CONSTRAINT "users_email_unique" UNIQUE("email");
COMMENT
ON COLUMN
    "Users"."status" IS 'Статус привилегий: user, admin, support (CS)';
CREATE TABLE "Areas"(
                        "id" BIGINT NOT NULL,
                        "name" VARCHAR(255) NOT NULL,
                        "description" TEXT NULL,
                        "type" VARCHAR(255) NOT NULL DEFAULT 'переговорка',
                        "features" VARCHAR(255) NOT NULL,
                        "capacity" INTEGER NOT NULL,
                        "Status" VARCHAR(255) NOT NULL DEFAULT 'in progress'
);
ALTER TABLE
    "Areas" ADD PRIMARY KEY("id");
COMMENT
ON COLUMN
    "Areas"."features" IS 'Уточнение к типу переговорки по фичам';
CREATE TABLE "Bookings"(
                           "id" BIGINT NOT NULL,
                           "user_id" BIGINT NOT NULL,
                           "area_id" BIGINT NOT NULL,
                           "start_time" TIME(0) WITHOUT TIME ZONE NOT NULL,
                           "end_time" TIME(0) WITHOUT TIME ZONE NOT NULL,
                           "quantity" INTEGER NOT NULL,
                           "status" VARCHAR(255) NOT NULL DEFAULT 'Ожидается',
                           "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "Bookings" ADD PRIMARY KEY("id");
CREATE INDEX "bookings_user_id_index" ON
    "Bookings"("user_id");
CREATE INDEX "bookings_area_id_index" ON
    "Bookings"("area_id");
CREATE TABLE "Reviews"(
                          "id" BIGINT NOT NULL,
                          "user_id" BIGINT NOT NULL,
                          "rating" SMALLINT NOT NULL,
                          "comment" TEXT NULL,
                          "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "Reviews" ADD PRIMARY KEY("id");
CREATE INDEX "reviews_user_id_index" ON
    "Reviews"("user_id");
CREATE TABLE "Tickets"(
                          "user_id" BIGINT NOT NULL,
                          "area_id" BIGINT NOT NULL,
                          "type" VARCHAR(255) NOT NULL,
                          "description" TEXT NOT NULL,
                          "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "Tickets" ADD PRIMARY KEY("user_id", "area_id");
CREATE TABLE "News"(
                       "id" BIGINT NOT NULL,
                       "title" VARCHAR(255) NOT NULL,
                       "description" TEXT NOT NULL,
                       "tags" VARCHAR(255) CHECK
                           ("tags" IN('')) NULL,
                       "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
ALTER TABLE
    "News" ADD PRIMARY KEY("id");
CREATE TABLE "Events"(
                         "id" BIGINT NOT NULL,
                         "name" VARCHAR(255) NOT NULL,
                         "description" TEXT NOT NULL,
                         "date" DATE NOT NULL
);
ALTER TABLE
    "Events" ADD PRIMARY KEY("id");
CREATE TABLE "Hall_Occupancy"(
                                 "date_time" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
                                 "reserved_places" BIGINT NOT NULL
);
ALTER TABLE
    "Hall_Occupancy" ADD PRIMARY KEY("date_time");
CREATE TABLE "Schedule"(
                           "day_off" DATE NOT NULL,
                           "description" TEXT NOT NULL,
                           "start_time" TIME(0) WITHOUT TIME ZONE NULL,
                           "stop_time" TIME(0) WITHOUT TIME ZONE NULL,
                           "tag" VARCHAR(255) CHECK
                               ("tag" IN('')) NOT NULL
);
ALTER TABLE
    "Schedule" ADD PRIMARY KEY("day_off");
ALTER TABLE
    "Reviews" ADD CONSTRAINT "reviews_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "Users"("id");
ALTER TABLE
    "Bookings" ADD CONSTRAINT "bookings_area_id_foreign" FOREIGN KEY("area_id") REFERENCES "Areas"("id");
ALTER TABLE
    "Bookings" ADD CONSTRAINT "bookings_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "Users"("id");
ALTER TABLE
    "Tickets" ADD CONSTRAINT "tickets_area_id_foreign" FOREIGN KEY("area_id") REFERENCES "Areas"("id");
ALTER TABLE
    "Tickets" ADD CONSTRAINT "tickets_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "Users"("id");