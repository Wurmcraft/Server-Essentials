-- Table: public.markets

-- DROP TABLE IF EXISTS public.markets;

CREATE TABLE IF NOT EXISTS public.markets
(
    server_id text COLLATE pg_catalog."default",
    seller_uuid text COLLATE pg_catalog."default",
    item text COLLATE pg_catalog."default",
    currency text COLLATE pg_catalog."default",
    "timestamp" text COLLATE pg_catalog."default",
    market_type text COLLATE pg_catalog."default",
    market_data text COLLATE pg_catalog."default",
    transfer_id text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;