CREATE TABLE `markets` (
  `serverID` tinytext NOT NULL,
  `sellerUUID` tinytext NOT NULL,
  `item` json NOT NULL,
  `currencyName` tinytext NOT NULL,
  `currencyAmount` decimal(65,4) NOT NULL,
  `timestamp` tinytext NOT NULL,
  `marketType` tinytext NOT NULL,
  `marketData` tinytext NOT NULL,
  `transferID` text
);
