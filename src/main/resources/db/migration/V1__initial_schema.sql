-- For MySQL use:  id INT NOT NULL AUTO_INCREMENT along with     PRIMARY KEY (id)
-- For PostgreSQL use: id SERIAL PRIMARY KEY,

CREATE TABLE "user"(
    id SERIAL PRIMARY KEY,
    email TEXT NOT NULL UNIQUE
);