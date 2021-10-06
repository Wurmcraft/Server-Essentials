CREATE TABLE `donator` (
  `store` text NOT NULL,
  `transaction_id` tinytext NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `uuid` tinytext NOT NULL,
  `timestamp` tinytext NOT NULL,
  `type` tinytext NOT NULL,
  `type_data` json NOT NULL
);
