INSERT INTO "Areas" ("id", "name", "description", "type", "features", "capacity", "status")
VALUES
    ('a1b2c3d4-e5f6-7890-1234-56789abcdef0', 'Open Space', 'Large open area with shared desks and comfortable seating.', 'WORKPLACE', '', 30, 'AVAILABLE'),
    ('b2c3d4e5-f678-9012-3456-789abcdef123', 'Meeting Room Alpha', 'Private meeting room with a projector and conference table.', 'MEETING_ROOM', 'PROJECTOR, WHITEBOARD, CONDITIONER', 10, 'BOOKED'),
    ('c3d4e5f6-7890-1234-5678-9abcdef12345', 'Quiet Zone', 'Dedicated silent workspace for focused work.', 'MEETING_ROOM', 'SILENT, TV', 10, 'AVAILABLE');


INSERT INTO "Users" ("id", "tg_id", "name", "email", "password_hash", "phone", "created_at", "status")
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 1234567890, 'Alice Johnson', 'alice@example.com', '$2b$12$abcdefghijklmnopqrstuv', 79123456789, '2025-04-03 12:00:00', 'active'),
    ('550e8400-e29b-41d4-a716-446655440001', 1987654321, 'Bob Smith', 'bob@example.com', '$2b$12$zyxwvutsrqponmlkjihgfedc', 79219876543, '2025-04-03 12:05:00', 'inactive'),
    ('550e8400-e29b-41d4-a716-446655440002', 8987654325, 'Charlie Davis', 'charlie@example.com', '$2b$12$1234567890abcdefgijklmn', 79239876543, '2025-04-03 12:10:00', 'pending');

INSERT INTO "News" ("id", "title", "description", "tags", "created_at")
VALUES
    ('a123b456-c789-0123-4567-89abcdef0123', 'Space X', 'Elon Musk visited a coworking space and launched a taxi service for delivery from anywhere in St. Petersburg.', ARRAY['TECHNOLOGY', 'MARKETING'], '2025-04-04 10:00:00'),
    ('c345d678-e901-2345-6789-abcdef234567', 'System Maintenance Scheduled', 'Our platform will undergo scheduled maintenance on April 10th from 2 AM to 4 AM. Some services may be temporarily unavailable.', NULL, '2025-04-03 08:15:00');

INSERT INTO "Events" ("id", "name", "description", "tags", "date", "available_places", "user_list")
VALUES
    ('b2c3d4e5-f678-9012-3456-789abcdef123', 'Startup Pitch Night', 'An opportunity for startups to present their ideas to investors.',  NULL,'2025-07-10', 30, ARRAY[]::json),
    ('c3d4e5f6-7890-1234-5678-9abcdef12345', 'AI Workshop', 'Hands-on workshop on building AI-powered applications.', NULL, '2025-08-20', 20, ARRAY[
        '{"id": "550e8400-e29b-41d4-a716-446655440000"}'::json,
     '{"id": "c3d4e5f6-7890-1234-5678-9abcdef12345"}'::json
    ]);