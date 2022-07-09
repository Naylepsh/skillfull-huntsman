CREATE TABLE IF NOT EXISTS requirements (
  name TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS offers (
  id UUID PRIMARY KEY,
  url TEXT NOT NULL,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  experience_level TEXT not NULL
);

CREATE TABLE IF NOT EXISTS offer_requirements (
  id INTEGER PRIMARY KEY,
  offer_id UUID NOT NULL,
  requirement_name TEXT NOT NULL,

  FOREIGN KEY (offer_id)
    REFERENCES offers (id)
  FOREIGN KEY (requirement_name)
    REFERENCES requirements (name)
);