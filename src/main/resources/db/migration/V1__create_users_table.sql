-- V1: tabela de usuários.
-- Escrita para rodar igual em H2 (MODE=PostgreSQL) e em PostgreSQL:
-- o UUID é gerado pela aplicação e as datas usam TIMESTAMP WITH TIME ZONE.
CREATE TABLE users (
    id                      UUID                     NOT NULL,
    full_name               VARCHAR(150)             NOT NULL,
    email                   VARCHAR(150)             NOT NULL,
    password_hash           VARCHAR(100)             NOT NULL,
    preferred_name          VARCHAR(50),
    time_zone               VARCHAR(50)              NOT NULL DEFAULT 'America/Sao_Paulo',
    status                  VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',
    terms_accepted_version  VARCHAR(20)              NOT NULL,
    terms_accepted_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_email_lower CHECK (email = LOWER(email)),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);
