CREATE TABLE IF NOT EXISTS registrations (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(320) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_registrations_username_length CHECK (char_length(trim(username)) BETWEEN 3 AND 50),
    CONSTRAINT chk_registrations_email_length CHECK (char_length(trim(email)) BETWEEN 6 AND 320),
    CONSTRAINT chk_registrations_password_hash CHECK (char_length(trim(password_hash)) > 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_registrations_email_lower
    ON registrations (LOWER(email));

CREATE UNIQUE INDEX IF NOT EXISTS uk_registrations_username_lower
    ON registrations (LOWER(username));

CREATE TABLE IF NOT EXISTS user_info (
    registration_id BIGINT PRIMARY KEY REFERENCES registrations(id) ON DELETE CASCADE,
    full_name VARCHAR(120) NOT NULL,
    age SMALLINT NOT NULL,
    gender VARCHAR(20),
    phone_number VARCHAR(25),
    health_info TEXT,
    daily_outdoor_activity VARCHAR(50),
    division VARCHAR(80) NOT NULL,
    district VARCHAR(80) NOT NULL,
    city VARCHAR(80) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_info_age CHECK (age BETWEEN 1 AND 120),
    CONSTRAINT chk_user_info_full_name CHECK (char_length(trim(full_name)) >= 2),
    CONSTRAINT chk_user_info_city CHECK (char_length(trim(city)) >= 2)
);

CREATE INDEX IF NOT EXISTS idx_user_info_location
    ON user_info (division, district, city);
