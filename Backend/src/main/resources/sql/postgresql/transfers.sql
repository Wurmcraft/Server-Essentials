-- Table: public.transfers

-- DROP TABLE IF EXISTS public.transfers;

CREATE TABLE IF NOT EXISTS public.transfers
(
    transfer_id text COLLATE pg_catalog."default",
    uuid text COLLATE pg_catalog."default",
    start_time text COLLATE pg_catalog."default",
    items text COLLATE pg_catalog."default",
    server_id text COLLATE pg_catalog."default",
    transfer_data text COLLATE pg_catalog."default"
)

    TABLESPACE pg_default;