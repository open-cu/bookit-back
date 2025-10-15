CREATE TABLE public.area_features (
                                      area_id uuid NOT NULL,
                                      features character varying(255),
                                      CONSTRAINT area_features_features_check CHECK (((features)::text = ANY ((ARRAY['SILENT'::character varying, 'LIGHT'::character varying, 'PROJECTOR'::character varying, 'WHITEBOARD'::character varying, 'TV'::character varying, 'CHANCELLERY'::character varying, 'CONDITIONER'::character varying, 'SPEAKER_SYSTEM'::character varying])::text[])))
);

CREATE TABLE public.area_keys (
                                  area_id uuid NOT NULL,
                                  keys character varying(255) NOT NULL
);

CREATE TABLE public.areas (
                              id uuid NOT NULL,
                              capacity integer NOT NULL,
                              description character varying(255),
                              name character varying(255) NOT NULL,
                              status character varying(255) NOT NULL,
                              type character varying(255) NOT NULL,
                              CONSTRAINT areas_status_check CHECK (((status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'UNAVAILABLE'::character varying, 'BOOKED'::character varying])::text[]))),
    CONSTRAINT areas_type_check CHECK (((type)::text = ANY ((ARRAY['LECTURE_HALL'::character varying, 'MEETING_ROOM'::character varying, 'WORKPLACE'::character varying])::text[])))
);

CREATE TABLE public.bookings (
                                 id uuid NOT NULL,
                                 created_at timestamp(6) without time zone NOT NULL,
                                 end_time timestamp(6) without time zone NOT NULL,
                                 quantity integer NOT NULL,
                                 start_time timestamp(6) without time zone NOT NULL,
                                 status character varying(255) NOT NULL,
                                 area_id uuid NOT NULL,
                                 user_id uuid NOT NULL,
                                 CONSTRAINT bookings_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'CONFIRMED'::character varying, 'CANCELED'::character varying, 'COMPLETED'::character varying])::text[])))
);

CREATE TABLE public.event_formats (
                                      event_id uuid NOT NULL,
                                      format character varying(255),
                                      CONSTRAINT event_formats_format_check CHECK (((format)::text = ANY ((ARRAY['WORKSHOP'::character varying, 'NETWORKING'::character varying, 'LECTURE'::character varying, 'MEETUP'::character varying, 'HACKATHON'::character varying, 'RELAX'::character varying, 'GAMES'::character varying])::text[])))
);

CREATE TABLE public.event_keys (
                                   event_id uuid NOT NULL,
                                   keys character varying(255) NOT NULL
);

CREATE TABLE public.event_notifications (
                                            id uuid NOT NULL,
                                            event_date_time timestamp(6) without time zone,
                                            event_id uuid NOT NULL,
                                            event_title character varying(255),
                                            message character varying(255),
                                            user_email character varying(255),
                                            user_id uuid NOT NULL
);

CREATE TABLE public.event_participation_formats (
                                                    event_id uuid NOT NULL,
                                                    participation_format character varying(255),
                                                    CONSTRAINT event_participation_formats_participation_format_check CHECK (((participation_format)::text = ANY ((ARRAY['ONLINE'::character varying, 'OFFLINE'::character varying, 'INDIVIDUAL'::character varying, 'HYBRID'::character varying, 'RECORDING'::character varying])::text[])))
);

CREATE TABLE public.event_tags (
                                   event_id uuid NOT NULL,
                                   tag character varying(255),
                                   CONSTRAINT event_tags_tag_check CHECK (((tag)::text = ANY ((ARRAY['PSYCHOLOGY'::character varying, 'ART'::character varying, 'MARKETING'::character varying, 'TECHNOLOGY'::character varying, 'BUSINESS'::character varying, 'SCIENCE'::character varying, 'IT'::character varying, 'SUCCESS_STORY'::character varying])::text[])))
);

CREATE TABLE public.event_times (
                                    event_id uuid NOT NULL,
                                    "time" character varying(255),
                                    CONSTRAINT event_times_time_check CHECK ((("time")::text = ANY ((ARRAY['MORNING'::character varying, 'DAY'::character varying, 'EVENING'::character varying, 'NIGHT'::character varying, 'WEEKDAYS'::character varying, 'WEEKENDS'::character varying])::text[])))
);

CREATE TABLE public.event_users (
                                    event_id uuid NOT NULL,
                                    user_id uuid NOT NULL
);


CREATE TABLE public.events (
                               id uuid NOT NULL,
                               available_places integer NOT NULL,
                               description character varying(255) NOT NULL,
                               end_time timestamp(6) without time zone,
                               name character varying(255) NOT NULL,
                               start_time timestamp(6) without time zone NOT NULL,
                               area_id uuid,
                               system_booking_id uuid
);

CREATE TABLE public.hall_occupancy (
                                       date_time timestamp(6) without time zone NOT NULL,
                                       reserved_places integer NOT NULL
);

CREATE TABLE public.news (
                             id uuid NOT NULL,
                             created_at timestamp(6) without time zone NOT NULL,
                             description text NOT NULL,
                             title character varying(255) NOT NULL
);


CREATE TABLE public.news_keys (
                                  news_id uuid NOT NULL,
                                  keys character varying(255) NOT NULL
);

CREATE TABLE public.news_tags (
                                  news_id uuid NOT NULL,
                                  tag character varying(255),
                                  CONSTRAINT news_tags_tag_check CHECK (((tag)::text = ANY ((ARRAY['PSYCHOLOGY'::character varying, 'ART'::character varying, 'MARKETING'::character varying, 'TECHNOLOGY'::character varying, 'BUSINESS'::character varying, 'SCIENCE'::character varying, 'IT'::character varying, 'SUCCESS_STORY'::character varying])::text[])))
);


CREATE TABLE public.reviews (
                                id uuid NOT NULL,
                                comment character varying(255),
                                created_at timestamp(6) without time zone NOT NULL,
                                rating smallint NOT NULL,
                                user_id uuid NOT NULL
);

CREATE TABLE public.schedule (
                                 day_off date NOT NULL,
                                 description character varying(255),
                                 start_time time(6) without time zone,
                                 stop_time time(6) without time zone,
                                 tag character varying(255) NOT NULL,
                                 CONSTRAINT schedule_tag_check CHECK (((tag)::text = ANY ((ARRAY['WEEKEND'::character varying, 'HOLIDAY'::character varying, 'TECHNICAL_WORK'::character varying, 'SANITARY_DAY'::character varying, 'PRIVATE_EVENT'::character varying, 'WEATHER_EMERGENCY'::character varying, 'QUARANTINE'::character varying, 'ELECTRICITY_OUTAGE'::character varying, 'UNDEFINED_REASON'::character varying])::text[])))
);

CREATE TABLE public.tickets (
                                id uuid NOT NULL,
                                closed_at timestamp(6) without time zone,
                                created_at timestamp(6) without time zone NOT NULL,
                                description text NOT NULL,
                                first_responded_at timestamp(6) without time zone,
                                priority character varying(255) NOT NULL,
                                reason character varying(255),
                                resolved_at timestamp(6) without time zone,
                                status character varying(255) NOT NULL,
                                type smallint NOT NULL,
                                updated_at timestamp(6) without time zone,
                                area_id uuid NOT NULL,
                                user_id uuid NOT NULL,
                                CONSTRAINT tickets_priority_check CHECK (((priority)::text = ANY ((ARRAY['DEFAULT'::character varying, 'LOW'::character varying, 'MEDIUM'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT tickets_status_check CHECK (((status)::text = ANY ((ARRAY['OPEN'::character varying, 'IN_PROGRESS'::character varying, 'ON_HOLD'::character varying, 'RESOLVED'::character varying, 'CLOSED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT tickets_type_check CHECK (((type >= 0) AND (type <= 3)))
);

CREATE TABLE public.user_roles (
                                   user_id uuid NOT NULL,
                                   roles character varying(255) NOT NULL,
                                   CONSTRAINT user_roles_roles_check CHECK (((roles)::text = ANY ((ARRAY['ROLE_USER'::character varying, 'ROLE_ADMIN'::character varying, 'ROLE_SUPERADMIN'::character varying])::text[])))
);

CREATE TABLE public.users (
                              id uuid NOT NULL,
                              created_at timestamp(6) without time zone NOT NULL,
                              email character varying(255),
                              first_name character varying(255) NOT NULL,
                              last_name character varying(255),
                              password_hash character varying(255),
                              phone character varying(255),
                              photo_url character varying(255),
                              status character varying(255) NOT NULL,
                              subscribed_to_notifications boolean DEFAULT true NOT NULL,
                              tg_id bigint,
                              updated_at timestamp(6) without time zone,
                              username character varying(255) NOT NULL,
                              CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['CREATED'::character varying, 'VERIFIED'::character varying, 'BANNED'::character varying, 'DELETED'::character varying])::text[])))
);

COPY public.area_features (area_id, features) FROM stdin;
c00be13b-67ca-46ba-a504-ad7c80c35ad1	CHANCELLERY
\.

COPY public.area_keys (area_id, keys) FROM stdin;
c00be13b-67ca-46ba-a504-ad7c80c35ad1	arch.png
80ceabfa-993a-49f0-8562-2413cb6c979c	arch.png
bfadc1ee-4ffc-442e-960d-cafee1ea4280	arch.png
\.

COPY public.areas (id, capacity, description, name, status, type) FROM stdin;
c00be13b-67ca-46ba-a504-ad7c80c35ad1	4	Большая открытая зона с общими столами и удобными сиденьями.	Open Space	AVAILABLE	WORKPLACE
80ceabfa-993a-49f0-8562-2413cb6c979c	10	Отдельная переговорная комната с проектором и конференц-столом.	Meeting Room Alpha	BOOKED	MEETING_ROOM
bfadc1ee-4ffc-442e-960d-cafee1ea4280	10	Выделенное тихое рабочее место для сосредоточенной работы.	Quiet Zone	AVAILABLE	MEETING_ROOM
\.

COPY public.bookings (id, created_at, end_time, quantity, start_time, status, area_id, user_id) FROM stdin;
73eda779-2954-41e3-bd62-568701da6059	2025-01-03 22:39:25.746173	2025-05-25 18:00:00	1	2025-05-25 16:00:00	CONFIRMED	c00be13b-67ca-46ba-a504-ad7c80c35ad1	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
2d5847c1-b4e2-419b-8ab0-86a035536c9a	2025-01-03 22:39:25.746173	2025-05-25 18:00:00	1	2025-05-25 16:00:00	CONFIRMED	bfadc1ee-4ffc-442e-960d-cafee1ea4280	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
24c62c1b-f7ee-4bce-9b46-53091c93ac00	2025-01-03 22:39:25.746173	2025-05-25 18:00:00	1	2025-05-25 16:00:00	CONFIRMED	80ceabfa-993a-49f0-8562-2413cb6c979c	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
fceb7d8d-9c3f-45b8-863f-cd44591e97b6	2025-01-03 22:39:25.746173	2025-01-23 18:00:00	1	2025-01-23 16:00:00	CONFIRMED	c00be13b-67ca-46ba-a504-ad7c80c35ad1	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
af2ffe0e-e40a-436f-8f6f-b77c342814a8	2025-01-03 22:39:25.746173	2025-01-23 18:00:00	1	2025-01-23 16:00:00	CONFIRMED	80ceabfa-993a-49f0-8562-2413cb6c979c	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
93ecae32-247e-4807-acd4-1f7fc619a1fa	2025-01-03 22:39:25.746173	2025-01-23 18:00:00	1	2025-01-23 16:00:00	CONFIRMED	bfadc1ee-4ffc-442e-960d-cafee1ea4280	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
7b85d126-d450-4512-860a-4d52a6494e8a	2025-04-03 22:39:25.746173	2025-08-18 23:00:00	1	2025-08-18 21:00:00	CONFIRMED	bfadc1ee-4ffc-442e-960d-cafee1ea4280	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
6c95ab27-ee12-4d27-a3c1-c79240919da8	2025-04-03 22:39:25.746173	2025-08-18 23:00:00	1	2025-08-18 21:00:00	CONFIRMED	80ceabfa-993a-49f0-8562-2413cb6c979c	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
1b61c642-26e0-498f-86df-93981c212194	2025-04-03 22:39:25.746173	2025-08-18 23:00:00	1	2025-08-18 21:00:00	CANCELED	c00be13b-67ca-46ba-a504-ad7c80c35ad1	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
ea97df2a-7827-4e43-acdb-c2e3e589a57e	2025-08-18 22:16:25.996075	2025-08-19 11:00:00	0	2025-08-19 10:00:00	CONFIRMED	c00be13b-67ca-46ba-a504-ad7c80c35ad1	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
5013acd1-8cb3-4255-9947-9de44c4f436d	2025-08-18 22:16:26.007478	2025-08-20 19:00:00	0	2025-08-20 18:00:00	CONFIRMED	c00be13b-67ca-46ba-a504-ad7c80c35ad1	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
76185cb7-176e-4c93-a592-51ee16566f4c	2025-08-18 22:16:26.016648	2025-08-20 21:00:00	0	2025-08-20 20:00:00	CONFIRMED	bfadc1ee-4ffc-442e-960d-cafee1ea4280	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
\.


COPY public.event_formats (event_id, format) FROM stdin;
\.

COPY public.event_keys (event_id, keys) FROM stdin;
9de77e91-464a-40aa-bfad-5fd06fc52c8c	arch.png
71faadfc-9723-4754-97e0-f5d6dc05cbb1	arch.png
9f602260-4a36-4476-a5c9-8e8dd6f8cad9	arch.png
\.


COPY public.event_notifications (id, event_date_time, event_id, event_title, message, user_email, user_id) FROM stdin;
\.


COPY public.event_participation_formats (event_id, participation_format) FROM stdin;
\.


COPY public.event_tags (event_id, tag) FROM stdin;
9de77e91-464a-40aa-bfad-5fd06fc52c8c	IT
9de77e91-464a-40aa-bfad-5fd06fc52c8c	TECHNOLOGY
71faadfc-9723-4754-97e0-f5d6dc05cbb1	IT
71faadfc-9723-4754-97e0-f5d6dc05cbb1	SCIENCE
9f602260-4a36-4476-a5c9-8e8dd6f8cad9	IT
9f602260-4a36-4476-a5c9-8e8dd6f8cad9	TECHNOLOGY
\.

COPY public.event_times (event_id, "time") FROM stdin;
\.

COPY public.event_users (event_id, user_id) FROM stdin;
9de77e91-464a-40aa-bfad-5fd06fc52c8c	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
9f602260-4a36-4476-a5c9-8e8dd6f8cad9	4495bfc7-0da4-4e23-a7ef-aa6dc7a45064
\.

COPY public.events (id, available_places, description, end_time, name, start_time, area_id, system_booking_id) FROM stdin;
9de77e91-464a-40aa-bfad-5fd06fc52c8c	30	Возможность для стартапов представить свои идеи инвесторам.	2025-08-19 11:00:00	Ночь презентаций стартапов	2025-08-19 10:00:00	c00be13b-67ca-46ba-a504-ad7c80c35ad1	ea97df2a-7827-4e43-acdb-c2e3e589a57e
71faadfc-9723-4754-97e0-f5d6dc05cbb1	0	Практический семинар по созданию приложений на базе искусственного интеллекта.	2025-08-20 19:00:00	Мастерская искусственного интеллекта	2025-08-20 18:00:00	c00be13b-67ca-46ba-a504-ad7c80c35ad1	5013acd1-8cb3-4255-9947-9de44c4f436d
9f602260-4a36-4476-a5c9-8e8dd6f8cad9	30	Возможность для стартапов представить свои идеи инвесторам.	2025-08-20 21:00:00	Мастерская искусственного интеллекта возвращается	2025-08-20 20:00:00	bfadc1ee-4ffc-442e-960d-cafee1ea4280	76185cb7-176e-4c93-a592-51ee16566f4c
\.

COPY public.hall_occupancy (date_time, reserved_places) FROM stdin;
2025-08-18 08:00:00	0
2025-08-18 09:00:00	0
2025-08-18 10:00:00	0
2025-08-18 11:00:00	0
2025-08-18 12:00:00	0
2025-08-18 13:00:00	0
2025-08-18 14:00:00	0
2025-08-18 15:00:00	0
2025-08-18 16:00:00	0
2025-08-18 17:00:00	0
2025-08-18 18:00:00	0
2025-08-18 19:00:00	0
2025-08-18 20:00:00	0
2025-08-19 08:00:00	0
2025-08-19 09:00:00	0
2025-08-19 10:00:00	0
2025-08-19 11:00:00	0
2025-08-19 12:00:00	0
2025-08-19 13:00:00	0
2025-08-19 14:00:00	0
2025-08-19 15:00:00	0
2025-08-19 16:00:00	0
2025-08-19 17:00:00	0
2025-08-19 18:00:00	0
2025-08-19 19:00:00	0
2025-08-19 20:00:00	0
2025-08-20 08:00:00	0
2025-08-20 09:00:00	0
2025-08-20 10:00:00	0
2025-08-20 11:00:00	0
2025-08-20 12:00:00	0
2025-08-20 13:00:00	0
2025-08-20 14:00:00	0
2025-08-20 15:00:00	0
2025-08-20 16:00:00	0
2025-08-20 17:00:00	0
2025-08-20 18:00:00	0
2025-08-20 19:00:00	0
2025-08-20 20:00:00	0
2025-08-21 08:00:00	0
2025-08-21 09:00:00	0
2025-08-21 10:00:00	0
2025-08-21 11:00:00	0
2025-08-21 12:00:00	0
2025-08-21 13:00:00	0
2025-08-21 14:00:00	0
2025-08-21 15:00:00	0
2025-08-21 16:00:00	0
2025-08-21 17:00:00	0
2025-08-21 18:00:00	0
2025-08-21 19:00:00	0
2025-08-21 20:00:00	0
2025-08-22 08:00:00	0
2025-08-22 09:00:00	0
2025-08-22 10:00:00	0
2025-08-22 11:00:00	0
2025-08-22 12:00:00	0
2025-08-22 13:00:00	0
2025-08-22 14:00:00	0
2025-08-22 15:00:00	0
2025-08-22 16:00:00	0
2025-08-22 17:00:00	0
2025-08-22 18:00:00	0
2025-08-22 19:00:00	0
2025-08-22 20:00:00	0
2025-08-23 08:00:00	0
2025-08-23 09:00:00	0
2025-08-23 10:00:00	0
2025-08-23 11:00:00	0
2025-08-23 12:00:00	0
2025-08-23 13:00:00	0
2025-08-23 14:00:00	0
2025-08-23 15:00:00	0
2025-08-23 16:00:00	0
2025-08-23 17:00:00	0
2025-08-23 18:00:00	0
2025-08-23 19:00:00	0
2025-08-23 20:00:00	0
2025-08-24 08:00:00	0
2025-08-24 09:00:00	0
2025-08-24 10:00:00	0
2025-08-24 11:00:00	0
2025-08-24 12:00:00	0
2025-08-24 13:00:00	0
2025-08-24 14:00:00	0
2025-08-24 15:00:00	0
2025-08-24 16:00:00	0
2025-08-24 17:00:00	0
2025-08-24 18:00:00	0
2025-08-24 19:00:00	0
2025-08-24 20:00:00	0
2025-08-25 08:00:00	0
2025-08-25 09:00:00	0
2025-08-25 10:00:00	0
2025-08-25 11:00:00	0
2025-08-25 12:00:00	0
2025-08-25 13:00:00	0
2025-08-25 14:00:00	0
2025-08-25 15:00:00	0
2025-08-25 16:00:00	0
2025-08-25 17:00:00	0
2025-08-25 18:00:00	0
2025-08-25 19:00:00	0
2025-08-25 20:00:00	0
2025-08-26 08:00:00	0
2025-08-26 09:00:00	0
2025-08-26 10:00:00	0
2025-08-26 11:00:00	0
2025-08-26 12:00:00	0
2025-08-26 13:00:00	0
2025-08-26 14:00:00	0
2025-08-26 15:00:00	0
2025-08-26 16:00:00	0
2025-08-26 17:00:00	0
2025-08-26 18:00:00	0
2025-08-26 19:00:00	0
2025-08-26 20:00:00	0
2025-08-27 08:00:00	0
2025-08-27 09:00:00	0
2025-08-27 10:00:00	0
2025-08-27 11:00:00	0
2025-08-27 12:00:00	0
2025-08-27 13:00:00	0
2025-08-27 14:00:00	0
2025-08-27 15:00:00	0
2025-08-27 16:00:00	0
2025-08-27 17:00:00	0
2025-08-27 18:00:00	0
2025-08-27 19:00:00	0
2025-08-27 20:00:00	0
2025-08-28 08:00:00	0
2025-08-28 09:00:00	0
2025-08-28 10:00:00	0
2025-08-28 11:00:00	0
2025-08-28 12:00:00	0
2025-08-28 13:00:00	0
2025-08-28 14:00:00	0
2025-08-28 15:00:00	0
2025-08-28 16:00:00	0
2025-08-28 17:00:00	0
2025-08-28 18:00:00	0
2025-08-28 19:00:00	0
2025-08-28 20:00:00	0
2025-08-29 08:00:00	0
2025-08-29 09:00:00	0
2025-08-29 10:00:00	0
2025-08-29 11:00:00	0
2025-08-29 12:00:00	0
2025-08-29 13:00:00	0
2025-08-29 14:00:00	0
2025-08-29 15:00:00	0
2025-08-29 16:00:00	0
2025-08-29 17:00:00	0
2025-08-29 18:00:00	0
2025-08-29 19:00:00	0
2025-08-29 20:00:00	0
2025-08-30 08:00:00	0
2025-08-30 09:00:00	0
2025-08-30 10:00:00	0
2025-08-30 11:00:00	0
2025-08-30 12:00:00	0
2025-08-30 13:00:00	0
2025-08-30 14:00:00	0
2025-08-30 15:00:00	0
2025-08-30 16:00:00	0
2025-08-30 17:00:00	0
2025-08-30 18:00:00	0
2025-08-30 19:00:00	0
2025-08-30 20:00:00	0
2025-08-31 08:00:00	0
2025-08-31 09:00:00	0
2025-08-31 10:00:00	0
2025-08-31 11:00:00	0
2025-08-31 12:00:00	0
2025-08-31 13:00:00	0
2025-08-31 14:00:00	0
2025-08-31 15:00:00	0
2025-08-31 16:00:00	0
2025-08-31 17:00:00	0
2025-08-31 18:00:00	0
2025-08-31 19:00:00	0
2025-08-31 20:00:00	0
2025-09-01 08:00:00	0
2025-09-01 09:00:00	0
2025-09-01 10:00:00	0
2025-09-01 11:00:00	0
2025-09-01 12:00:00	0
2025-09-01 13:00:00	0
2025-09-01 14:00:00	0
2025-09-01 15:00:00	0
2025-09-01 16:00:00	0
2025-09-01 17:00:00	0
2025-09-01 18:00:00	0
2025-09-01 19:00:00	0
2025-09-01 20:00:00	0
2025-09-02 08:00:00	0
2025-09-02 09:00:00	0
2025-09-02 10:00:00	0
2025-09-02 11:00:00	0
2025-09-02 12:00:00	0
2025-09-02 13:00:00	0
2025-09-02 14:00:00	0
2025-09-02 15:00:00	0
2025-09-02 16:00:00	0
2025-09-02 17:00:00	0
2025-09-02 18:00:00	0
2025-09-02 19:00:00	0
2025-09-02 20:00:00	0
2025-09-03 08:00:00	0
2025-09-03 09:00:00	0
2025-09-03 10:00:00	0
2025-09-03 11:00:00	0
2025-09-03 12:00:00	0
2025-09-03 13:00:00	0
2025-09-03 14:00:00	0
2025-09-03 15:00:00	0
2025-09-03 16:00:00	0
2025-09-03 17:00:00	0
2025-09-03 18:00:00	0
2025-09-03 19:00:00	0
2025-09-03 20:00:00	0
2025-09-04 08:00:00	0
2025-09-04 09:00:00	0
2025-09-04 10:00:00	0
2025-09-04 11:00:00	0
2025-09-04 12:00:00	0
2025-09-04 13:00:00	0
2025-09-04 14:00:00	0
2025-09-04 15:00:00	0
2025-09-04 16:00:00	0
2025-09-04 17:00:00	0
2025-09-04 18:00:00	0
2025-09-04 19:00:00	0
2025-09-04 20:00:00	0
2025-09-05 08:00:00	0
2025-09-05 09:00:00	0
2025-09-05 10:00:00	0
2025-09-05 11:00:00	0
2025-09-05 12:00:00	0
2025-09-05 13:00:00	0
2025-09-05 14:00:00	0
2025-09-05 15:00:00	0
2025-09-05 16:00:00	0
2025-09-05 17:00:00	0
2025-09-05 18:00:00	0
2025-09-05 19:00:00	0
2025-09-05 20:00:00	0
2025-09-06 08:00:00	0
2025-09-06 09:00:00	0
2025-09-06 10:00:00	0
2025-09-06 11:00:00	0
2025-09-06 12:00:00	0
2025-09-06 13:00:00	0
2025-09-06 14:00:00	0
2025-09-06 15:00:00	0
2025-09-06 16:00:00	0
2025-09-06 17:00:00	0
2025-09-06 18:00:00	0
2025-09-06 19:00:00	0
2025-09-06 20:00:00	0
2025-09-07 08:00:00	0
2025-09-07 09:00:00	0
2025-09-07 10:00:00	0
2025-09-07 11:00:00	0
2025-09-07 12:00:00	0
2025-09-07 13:00:00	0
2025-09-07 14:00:00	0
2025-09-07 15:00:00	0
2025-09-07 16:00:00	0
2025-09-07 17:00:00	0
2025-09-07 18:00:00	0
2025-09-07 19:00:00	0
2025-09-07 20:00:00	0
2025-09-08 08:00:00	0
2025-09-08 09:00:00	0
2025-09-08 10:00:00	0
2025-09-08 11:00:00	0
2025-09-08 12:00:00	0
2025-09-08 13:00:00	0
2025-09-08 14:00:00	0
2025-09-08 15:00:00	0
2025-09-08 16:00:00	0
2025-09-08 17:00:00	0
2025-09-08 18:00:00	0
2025-09-08 19:00:00	0
2025-09-08 20:00:00	0
2025-09-09 08:00:00	0
2025-09-09 09:00:00	0
2025-09-09 10:00:00	0
2025-09-09 11:00:00	0
2025-09-09 12:00:00	0
2025-09-09 13:00:00	0
2025-09-09 14:00:00	0
2025-09-09 15:00:00	0
2025-09-09 16:00:00	0
2025-09-09 17:00:00	0
2025-09-09 18:00:00	0
2025-09-09 19:00:00	0
2025-09-09 20:00:00	0
2025-09-10 08:00:00	0
2025-09-10 09:00:00	0
2025-09-10 10:00:00	0
2025-09-10 11:00:00	0
2025-09-10 12:00:00	0
2025-09-10 13:00:00	0
2025-09-10 14:00:00	0
2025-09-10 15:00:00	0
2025-09-10 16:00:00	0
2025-09-10 17:00:00	0
2025-09-10 18:00:00	0
2025-09-10 19:00:00	0
2025-09-10 20:00:00	0
2025-09-11 08:00:00	0
2025-09-11 09:00:00	0
2025-09-11 10:00:00	0
2025-09-11 11:00:00	0
2025-09-11 12:00:00	0
2025-09-11 13:00:00	0
2025-09-11 14:00:00	0
2025-09-11 15:00:00	0
2025-09-11 16:00:00	0
2025-09-11 17:00:00	0
2025-09-11 18:00:00	0
2025-09-11 19:00:00	0
2025-09-11 20:00:00	0
2025-09-12 08:00:00	0
2025-09-12 09:00:00	0
2025-09-12 10:00:00	0
2025-09-12 11:00:00	0
2025-09-12 12:00:00	0
2025-09-12 13:00:00	0
2025-09-12 14:00:00	0
2025-09-12 15:00:00	0
2025-09-12 16:00:00	0
2025-09-12 17:00:00	0
2025-09-12 18:00:00	0
2025-09-12 19:00:00	0
2025-09-12 20:00:00	0
2025-09-13 08:00:00	0
2025-09-13 09:00:00	0
2025-09-13 10:00:00	0
2025-09-13 11:00:00	0
2025-09-13 12:00:00	0
2025-09-13 13:00:00	0
2025-09-13 14:00:00	0
2025-09-13 15:00:00	0
2025-09-13 16:00:00	0
2025-09-13 17:00:00	0
2025-09-13 18:00:00	0
2025-09-13 19:00:00	0
2025-09-13 20:00:00	0
2025-09-14 08:00:00	0
2025-09-14 09:00:00	0
2025-09-14 10:00:00	0
2025-09-14 11:00:00	0
2025-09-14 12:00:00	0
2025-09-14 13:00:00	0
2025-09-14 14:00:00	0
2025-09-14 15:00:00	0
2025-09-14 16:00:00	0
2025-09-14 17:00:00	0
2025-09-14 18:00:00	0
2025-09-14 19:00:00	0
2025-09-14 20:00:00	0
2025-09-15 08:00:00	0
2025-09-15 09:00:00	0
2025-09-15 10:00:00	0
2025-09-15 11:00:00	0
2025-09-15 12:00:00	0
2025-09-15 13:00:00	0
2025-09-15 14:00:00	0
2025-09-15 15:00:00	0
2025-09-15 16:00:00	0
2025-09-15 17:00:00	0
2025-09-15 18:00:00	0
2025-09-15 19:00:00	0
2025-09-15 20:00:00	0
2025-09-16 08:00:00	0
2025-09-16 09:00:00	0
2025-09-16 10:00:00	0
2025-09-16 11:00:00	0
2025-09-16 12:00:00	0
2025-09-16 13:00:00	0
2025-09-16 14:00:00	0
2025-09-16 15:00:00	0
2025-09-16 16:00:00	0
2025-09-16 17:00:00	0
2025-09-16 18:00:00	0
2025-09-16 19:00:00	0
2025-09-16 20:00:00	0
2025-09-17 08:00:00	0
2025-09-17 09:00:00	0
2025-09-17 10:00:00	0
2025-09-17 11:00:00	0
2025-09-17 12:00:00	0
2025-09-17 13:00:00	0
2025-09-17 14:00:00	0
2025-09-17 15:00:00	0
2025-09-17 16:00:00	0
2025-09-17 17:00:00	0
2025-09-17 18:00:00	0
2025-09-17 19:00:00	0
2025-09-17 20:00:00	0
\.

COPY public.news (id, created_at, description, title) FROM stdin;
c7e15ebe-371e-4507-a1c4-003ced780cba	2025-04-04 10:00:00	16605	Space X
d0d4f5b9-518a-4a1b-8ed0-2ad36eabe71d	2025-04-03 08:15:00	16606	Плановое обслуживание системы
\.

COPY public.news_keys (news_id, keys) FROM stdin;
c7e15ebe-371e-4507-a1c4-003ced780cba	arch.png
d0d4f5b9-518a-4a1b-8ed0-2ad36eabe71d	arch.png
\.

COPY public.news_tags (news_id, tag) FROM stdin;
c7e15ebe-371e-4507-a1c4-003ced780cba	IT
c7e15ebe-371e-4507-a1c4-003ced780cba	TECHNOLOGY
d0d4f5b9-518a-4a1b-8ed0-2ad36eabe71d	SCIENCE
d0d4f5b9-518a-4a1b-8ed0-2ad36eabe71d	TECHNOLOGY
\.

COPY public.reviews (id, comment, created_at, rating, user_id) FROM stdin;
\.

COPY public.schedule (day_off, description, start_time, stop_time, tag) FROM stdin;
2025-08-23	Выходной день	\N	\N	WEEKEND
2025-08-30	Выходной день	\N	\N	WEEKEND
2025-09-06	Выходной день	\N	\N	WEEKEND
2025-09-13	Выходной день	\N	\N	WEEKEND
\.

COPY public.tickets (id, closed_at, created_at, description, first_responded_at, priority, reason, resolved_at, status, type, updated_at, area_id, user_id) FROM stdin;
\.

COPY public.user_roles (user_id, roles) FROM stdin;
4495bfc7-0da4-4e23-a7ef-aa6dc7a45064	ROLE_USER
76a24fdc-350f-4142-9d8f-35364277a40a	ROLE_USER
571ee304-bea9-47ae-9cfc-c625391ed189	ROLE_USER
\.

COPY public.users (id, created_at, email, first_name, last_name, password_hash, phone, photo_url, status, subscribed_to_notifications, tg_id, updated_at, username) FROM stdin;
4495bfc7-0da4-4e23-a7ef-aa6dc7a45064	2025-04-03 12:00:00	alice@example.com	Alice	Johnson	$2b$12$abcdefghijklmnopqrstuv	+79123456789	\N	CREATED	f	1234567890	\N	alicejohnson
76a24fdc-350f-4142-9d8f-35364277a40a	2025-04-03 12:05:00	bob@example.com	Bob	Smith	$2b$12$zyxwvutsrqponmlkjihgfedc	+79219876543	\N	CREATED	f	1987654321	\N	bobsmith
571ee304-bea9-47ae-9cfc-c625391ed189	2025-04-03 12:10:00	charlie@example.com	Charlie Davis	Davis	$2b$12$1234567890abcdefgijklmn	+79219876542	\N	BANNED	f	8987654325	\N	charliedavis
\.

ALTER TABLE ONLY public.areas
    ADD CONSTRAINT areas_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT bookings_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.event_notifications
    ADD CONSTRAINT event_notifications_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.event_users
    ADD CONSTRAINT event_users_pkey PRIMARY KEY (event_id, user_id);

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.hall_occupancy
    ADD CONSTRAINT hall_occupancy_pkey PRIMARY KEY (date_time);

ALTER TABLE ONLY public.news
    ADD CONSTRAINT news_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.reviews
    ADD CONSTRAINT reviews_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (day_off);

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


ALTER TABLE ONLY public.events
    ADD CONSTRAINT ukaitrt92bh0x5pvl10e071ko8q UNIQUE (system_booking_id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukdu5v5sr43g5bfnji4vb8hg5s3 UNIQUE (phone);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, roles);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT fk27oijoa8m2uwor0mity3vtsy6 FOREIGN KEY (area_id) REFERENCES public.areas(id);

ALTER TABLE ONLY public.news_keys
    ADD CONSTRAINT fk339m6afsq2rjajrt16a0yh4s2 FOREIGN KEY (news_id) REFERENCES public.news(id);

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fk3y74ksy8rni02f0dsx6ehnp8w FOREIGN KEY (area_id) REFERENCES public.areas(id);

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT fk4eqsebpimnjen0q46ja6fl2hl FOREIGN KEY (user_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.events
    ADD CONSTRAINT fk52fhvwnwcfv7ac287rgh5wqyy FOREIGN KEY (system_booking_id) REFERENCES public.bookings(id);

ALTER TABLE ONLY public.event_times
    ADD CONSTRAINT fk8r9w1h5uhrthhblygna40vlw2 FOREIGN KEY (event_id) REFERENCES public.events(id);

ALTER TABLE ONLY public.reviews
    ADD CONSTRAINT fkcgy7qjc1r99dp117y9en6lxye FOREIGN KEY (user_id) REFERENCES public.users(id);


ALTER TABLE ONLY public.event_users
    ADD CONSTRAINT fkci0b9ys3awpour3lsn05dq8r4 FOREIGN KEY (event_id) REFERENCES public.events(id);

ALTER TABLE ONLY public.area_features
    ADD CONSTRAINT fkdlmnbh0hj98oj33qd04fongbf FOREIGN KEY (area_id) REFERENCES public.areas(id);

ALTER TABLE ONLY public.area_keys
    ADD CONSTRAINT fkdmib6h2v5dm5fueyu4e5kc5se FOREIGN KEY (area_id) REFERENCES public.areas(id);

ALTER TABLE ONLY public.bookings
    ADD CONSTRAINT fkeyog2oic85xg7hsu2je2lx3s6 FOREIGN KEY (user_id) REFERENCES public.users(id);


ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.event_keys
    ADD CONSTRAINT fkhlneno8jhnvw814afs0l9i8mb FOREIGN KEY (event_id) REFERENCES public.events(id);

ALTER TABLE ONLY public.news_tags
    ADD CONSTRAINT fki06sdgpsvq2oxtharq5q1rc3x FOREIGN KEY (news_id) REFERENCES public.news(id);

ALTER TABLE ONLY public.event_tags
    ADD CONSTRAINT fkiwoyitw224ykom58m5xnoa9y6 FOREIGN KEY (event_id) REFERENCES public.events(id);

ALTER TABLE ONLY public.event_users
    ADD CONSTRAINT fkl0jar0mnl3hqk84wwio9u8cmy FOREIGN KEY (user_id) REFERENCES public.users(id);

ALTER TABLE ONLY public.event_participation_formats
    ADD CONSTRAINT fkoctvyvcfge6ce023oisrx5su9 FOREIGN KEY (event_id) REFERENCES public.events(id);


ALTER TABLE ONLY public.event_formats
    ADD CONSTRAINT fkopc2wurgdl2b4ji0k1cgmc3rn FOREIGN KEY (event_id) REFERENCES public.events(id);

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT fksv8tolhtksv43g34bq2gijynr FOREIGN KEY (area_id) REFERENCES public.areas(id);


