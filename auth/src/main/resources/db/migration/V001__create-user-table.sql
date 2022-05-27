CREATE TABLE IF NOT EXISTS public.user
(
    uuid uuid NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    PRIMARY KEY (uuid)
);
