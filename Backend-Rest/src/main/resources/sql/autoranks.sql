CREATE TABLE `autoranks` (
  `autoRankID` smallint(6) NOT NULL,
  `rank` tinytext NOT NULL,
  `nextRank` tinytext NOT NULL,
  `playTime` smallint(6) NOT NULL,
  `currencyName` tinytext NOT NULL,
  `currencyAmount` int(11) NOT NULL,
  `specialEvents` json NOT NULL,
   PRIMARY KEY (`autoRankID`)
);
