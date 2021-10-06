CREATE TABLE `markets` (
  `server_id` tinytext NOT NULL,
  `seller_uuid` tinytext NOT NULL,
  `item` json NOT NULL,
  `currency_name` tinytext NOT NULL,
  `currency_amount` decimal(65,4) NOT NULL,
  `timestamp` tinytext NOT NULL,
  `market_type` tinytext NOT NULL,
  `market_data` tinytext NOT NULL,
  `transfer_id` text
);
