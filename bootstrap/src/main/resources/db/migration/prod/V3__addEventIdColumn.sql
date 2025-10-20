ALTER TABLE public.bookings
    ADD COLUMN IF NOT EXISTS event_id uuid;

ALTER TABLE public.bookings
    ADD CONSTRAINT FK_bookings_events
        FOREIGN KEY (EVENT_ID) REFERENCES public.events(ID);