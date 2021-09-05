CREATE TABLE markets (
  serverID text NOT NULL,
  sellerUUID text NOT NULL,
  item json NOT NULL,
  currencyName text NOT NULL,
  currencyAmount decimal(65,4) NOT NULL,
  timestamp text NOT NULL,
  marketType text NOT NULL,
  marketData text NOT NULL,
  transferID text
);
