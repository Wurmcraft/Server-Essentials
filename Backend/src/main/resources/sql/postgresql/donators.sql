-- Table: public.donators

-- DROP TABLE IF EXISTS public.donators;

CREATE TABLE IF NOT EXISTS public.donators
(
    store text COLLATE pg_catalog."default",
    transaction_id text COLLATE pg_catalog."default",
    amount text COLLATE pg_catalog."default",
    uuid text COLLATE pg_catalog."default",
    "timestamp" text COLLATE pg_catalog."default",
    type text COLLATE pg_catalog."default",
    type_data text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;