-- Временная откатка: удаляем ENUM если существует
DROP TYPE IF EXISTS user_role CASCADE;

-- Создаем новую таблицу с VARCHAR вместо ENUM
CREATE TABLE IF NOT EXISTS "public"."user_roles_new" (
     "user_id" UUID NOT NULL,
     "roles" VARCHAR(50) NOT NULL CHECK (roles IN ('ROLE_ADMIN', 'ROLE_SUPERADMIN', 'ROLE_USER', 'ROLE_SYSTEM_USER'))
    );

-- Копируем данные
INSERT INTO "public"."user_roles_new" ("user_id", "roles")
SELECT "user_id", "roles" FROM "public"."user_roles";

-- Удаляем старую таблицу
DROP TABLE IF EXISTS "public"."user_roles";

-- Переименовываем новую таблицу
ALTER TABLE "public"."user_roles_new" RENAME TO "user_roles";

-- Добавление пользователя с UUID '00000000-0000-0000-0000-000000000001' (UUID 1)
INSERT INTO "public"."users" (
    "id", "created_at", "email", "first_name", "last_name",
    "phone", "photo_url", "status", "subscribed_to_notifications", "tg_id",
    "updated_at", "username"
) VALUES (
             UUID '00000000-0000-0000-0000-000000000001',
             CURRENT_TIMESTAMP,
             'systemuser@example.com',
             'System',
             'User',
             '+70000000000',
             NULL,
             'VERIFIED',
             TRUE,
             NULL,
             NULL,
             'systemuser'
         );

-- Добавление ролей пользователя
INSERT INTO "public"."user_roles" ("user_id", "roles") VALUES
       (UUID '00000000-0000-0000-0000-000000000001', 'ROLE_SYSTEM_USER'),
       (UUID '00000000-0000-0000-0000-000000000001', 'ROLE_ADMIN'),
       (UUID '00000000-0000-0000-0000-000000000001', 'ROLE_SUPERADMIN');

-- Добавляем индексы
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON "public"."user_roles" ("user_id");
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON "public"."user_roles" ("roles");