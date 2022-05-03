-- Table: public.world-logs

-- DROP TABLE IF EXISTS public."world-logs";

CREATE TABLE IF NOT EXISTS public."world-logs"
(
    server_id text COLLATE pg_catalog."default",
    "timestamp" text COLLATE pg_catalog."default",
    action_type text COLLATE pg_catalog."default",
    action_data text COLLATE pg_catalog."default",
    uuid text COLLATE pg_catalog."default",
    x text COLLATE pg_catalog."default",
    y text COLLATE pg_catalog."default",
    z text COLLATE pg_catalog."default",
    dim text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;