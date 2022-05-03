-- Table: public.actions

-- DROP TABLE IF EXISTS public.actions;

CREATE TABLE IF NOT EXISTS public.actions
(
    related_id text COLLATE pg_catalog."default",
    host text COLLATE pg_catalog."default",
    action text COLLATE pg_catalog."default",
    action_data text COLLATE pg_catalog."default",
    "timestamp" text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;