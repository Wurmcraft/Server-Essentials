-- Table: public.users

-- DROP TABLE IF EXISTS public.users;

CREATE TABLE IF NOT EXISTS public.users
(
    uuid text COLLATE pg_catalog."default" NOT NULL,
    username text COLLATE pg_catalog."default" NOT NULL,
    ranks text[] COLLATE pg_catalog."default",
    perms text[] COLLATE pg_catalog."default",
    perks text[] COLLATE pg_catalog."default",
    language text COLLATE pg_catalog."default",
    muted boolean,
    nickname text COLLATE pg_catalog."default",
    discord_id text COLLATE pg_catalog."default",
    playtime json,
    wallet json,
    password_hash text COLLATE pg_catalog."default",
    password_salt text COLLATE pg_catalog."default",
    system_perms text[] COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (uuid)
    )

    TABLESPACE pg_default;