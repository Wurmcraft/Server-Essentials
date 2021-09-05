CREATE TABLE ranks (
  rankID SERIAL NOT NULL,
  name text NOT NULL,
  permissions text NOT NULL,
  inheritance text NOT NULL,
  prefix text NOT NULL,
  prefixPriority int NOT NULL DEFAULT '0',
  suffix text NOT NULL,
  suffixPriority int NOT NULL DEFAULT '0',
  color text,
  colorPriority int NOT NULL DEFAULT '0',
  PRIMARY KEY (rankID)
);
