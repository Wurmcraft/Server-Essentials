-- Table: public.currencies

-- DROP TABLE IF EXISTS public.currencies;

CREATE TABLE IF NOT EXISTS public.currencies
(
    display_name text COLLATE pg_catalog."default",
    global_worth text COLLATE pg_catalog."default",
    sell_worth text COLLATE pg_catalog."default",
    tax text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;