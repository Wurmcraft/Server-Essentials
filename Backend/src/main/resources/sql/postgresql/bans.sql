-- Table: public.bans

-- DROP TABLE IF EXISTS public.bans;

CREATE TABLE IF NOT EXISTS public.bans
(
    uuid text COLLATE pg_catalog."default",
    ip text COLLATE pg_catalog."default",
    discord_id text COLLATE pg_catalog."default",
    banned_by text COLLATE pg_catalog."default",
    ban_reason text COLLATE pg_catalog."default",
    "timestamp" text COLLATE pg_catalog."default",
    ban_type text COLLATE pg_catalog."default",
    ban_data text COLLATE pg_catalog."default",
    ban_status text COLLATE pg_catalog."default"
)

TABLESPACE pg_default;