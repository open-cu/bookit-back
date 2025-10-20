ALTER TABLE public.events
    ADD COLUMN IF NOT EXISTS requires_application boolean NOT NULL DEFAULT TRUE;

COMMENT ON COLUMN public.events.requires_application IS 'Флаг: для участия требуется заявка пользователя';

