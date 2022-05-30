CREATE TABLE IF NOT EXISTS public.event
(
    uuid uuid NOT NULL,
    name character varying NOT NULL,
    address character varying,
    description character varying,
    "time" timestamp without time zone,
    owner uuid NOT NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT owner_fk FOREIGN KEY (owner)
        REFERENCES public.profile (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);