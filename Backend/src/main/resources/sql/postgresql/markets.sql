CREATE TABLE markets (
  server_id text NOT NULL,
  seller_uuid text NOT NULL,
  item text NOT NULL,
  currency_name text NOT NULL,
  currency_amount decimal(65,4) NOT NULL,
  timestamp text NOT NULL,
  market_type text NOT NULL,
  market_data text NOT NULL,
  transfer_id text
);
