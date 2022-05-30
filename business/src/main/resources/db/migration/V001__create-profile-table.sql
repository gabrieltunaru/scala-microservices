CREATE TABLE IF NOT EXISTS public.profile
(
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    name character varying,
    location character varying,
    PRIMARY KEY (id)
);