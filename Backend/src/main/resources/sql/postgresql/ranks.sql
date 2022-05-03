-- Table: public.ranks

-- DROP TABLE IF EXISTS public.ranks;

CREATE TABLE IF NOT EXISTS public.ranks
(
    name text COLLATE pg_catalog."default",
    permissions text COLLATE pg_catalog."default",
    inheritance text COLLATE pg_catalog."default",
    prefix text COLLATE pg_catalog."default",
    prefix_priority integer,
    suffix text COLLATE pg_catalog."default",
    suffix_priority integer,
    color text COLLATE pg_catalog."default",
    color_priority text COLLATE pg_catalog."default",
    active_servers text COLLATE pg_catalog."default",
    protected boolean
)

    TABLESPACE pg_default;