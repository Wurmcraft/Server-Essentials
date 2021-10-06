CREATE TABLE ranks (
  name text NOT NULL,
  permissions text NOT NULL,
  inheritance text NOT NULL,
  prefix text NOT NULL,
  prefix_priority int NOT NULL DEFAULT '0',
  suffix text NOT NULL,
  suffix_priority int NOT NULL DEFAULT '0',
  color text,
  color_priority int NOT NULL DEFAULT '0',
  PRIMARY KEY (rankID)
);
