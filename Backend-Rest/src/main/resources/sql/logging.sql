CREATE TABLE `logging` (
  `serverID` tinytext NOT NULL,
  `timestamp` tinytext NOT NULL,
  `actionType` tinytext NOT NULL,
  `actionData` json NOT NULL,
  `uuid` tinytext NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `dim` smallint(6) NOT NULL
);
