-- Table: public.statistics

-- DROP TABLE IF EXISTS public.statistics;

CREATE TABLE IF NOT EXISTS public.statistics
(
    server_id text COLLATE pg_catalog."default",
    uuid text COLLATE pg_catalog."default",
    "timestamp" text COLLATE pg_catalog."default",
    event_type text COLLATE pg_catalog."default",
    event_data text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;