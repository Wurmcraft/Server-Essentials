CREATE TABLE `transfers` (
  `transferID` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` tinytext NOT NULL,
  `startTime` tinytext NOT NULL,
  `items` json NOT NULL,
  `serverID` tinytext NOT NULL,
  PRIMARY KEY (`transferID`)
);
