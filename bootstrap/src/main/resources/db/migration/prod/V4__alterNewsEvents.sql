
ALTER TABLE public.news
    ADD COLUMN short_description VARCHAR(500);

ALTER TABLE public.news
    RENAME COLUMN description TO full_description;

ALTER TABLE public.news
ALTER COLUMN full_description TYPE VARCHAR(2000);

COMMENT ON COLUMN public.news.full_description IS 'Полное описание новости (до 2000 символов)';
COMMENT ON COLUMN public.news.short_description IS 'Краткое описание новости (до 500 символов)';

ALTER TABLE public.events
    ADD COLUMN short_description VARCHAR(500);

ALTER TABLE public.events
    RENAME COLUMN description TO full_description;

ALTER TABLE public.events
ALTER COLUMN full_description TYPE VARCHAR(2000);

COMMENT ON COLUMN public.events.full_description IS 'Полное описание новости (до 2000 символов)';
COMMENT ON COLUMN public.events."short_description" IS 'Краткое описание новости (до 500 символов)';