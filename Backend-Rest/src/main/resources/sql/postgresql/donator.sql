CREATE TABLE donator (
  store text NOT NULL,
  transactionID text NOT NULL,
  amount decimal(10,2) NOT NULL,
  uuid text NOT NULL,
  timestamp text NOT NULL,
  type text NOT NULL,
  typeData json NOT NULL
);
