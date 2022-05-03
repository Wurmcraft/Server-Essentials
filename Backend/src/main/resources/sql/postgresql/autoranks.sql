-- Table: public.autoranks

-- DROP TABLE IF EXISTS public.autoranks;

CREATE TABLE IF NOT EXISTS public.autoranks
(
    title text COLLATE pg_catalog."default",
    rank text COLLATE pg_catalog."default",
    next_rank text COLLATE pg_catalog."default",
    playtime text COLLATE pg_catalog."default",
    currency text COLLATE pg_catalog."default",
    special_events text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;