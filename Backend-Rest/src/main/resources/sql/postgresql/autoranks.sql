CREATE TABLE autoranks (
  autoRankID int NOT NULL,
  rank text NOT NULL,
  nextRank text NOT NULL,
  playTime int NOT NULL,
  currencyName text NOT NULL,
  currencyAmount int NOT NULL,
  specialEvents json NOT NULL,
   PRIMARY KEY (autoRankID)
);
