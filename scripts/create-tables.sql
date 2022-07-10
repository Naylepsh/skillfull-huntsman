CREATE TABLE IF NOT EXISTS requirements (
  name TEXT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS offers (
  url TEXT PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  experience_level TEXT not NULL
);

CREATE TABLE IF NOT EXISTS offer_requirements (
  id INTEGER PRIMARY KEY,
  offer_url text NOT NULL,
  requirement_name TEXT NOT NULL,
  level INTEGER,

  FOREIGN KEY (offer_url)
    REFERENCES offers (url)
  FOREIGN KEY (requirement_name)
    REFERENCES requirements (name)
);