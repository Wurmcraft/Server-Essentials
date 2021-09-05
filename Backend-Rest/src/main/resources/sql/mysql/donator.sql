CREATE TABLE `donator` (
  `store` text NOT NULL,
  `transactionID` tinytext NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `uuid` tinytext NOT NULL,
  `timestamp` tinytext NOT NULL,
  `type` tinytext NOT NULL,
  `typeData` json NOT NULL
);
