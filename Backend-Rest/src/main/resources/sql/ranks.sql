CREATE TABLE `ranks` (
  `rankID` smallint(6) NOT NULL AUTO_INCREMENT,
  `name` tinytext NOT NULL,
  `permissions` text NOT NULL,
  `inheritance` text NOT NULL,
  `prefix` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `prefixPriority` smallint(4) NOT NULL DEFAULT '0',
  `suffix` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `suffixPriority` smallint(4) NOT NULL DEFAULT '0',
  `color` text,
  `colorPriority` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`rankID`)
);
