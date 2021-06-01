CREATE TABLE `statistics` (
  `serverID` tinytext NOT NULL,
  `uuid` tinytext NOT NULL,
  `timestamp` tinytext NOT NULL,
  `eventType` tinytext NOT NULL,
  `eventData` json NOT NULL
);
