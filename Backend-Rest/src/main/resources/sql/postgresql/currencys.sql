CREATE TABLE currencys (
  currencyID SERIAL NOT NULL,
  displayName text NOT NULL,
  globalWorth decimal(10,4) NOT NULL,
  sellWorth decimal(10,4) NOT NULL,
  tax decimal(10,2) NOT NULL,
  PRIMARY KEY (currencyID)
);
