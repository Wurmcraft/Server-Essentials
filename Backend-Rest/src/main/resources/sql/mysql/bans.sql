CREATE TABLE `bans` (
  `banID` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` tinytext NOT NULL,
  `ip` text NOT NULL,
  `discordID` text,
  `bannedBy` tinytext NOT NULL,
  `bannedByType` text NOT NULL,
  `banReason` text NOT NULL,
  `timestamp` tinytext NOT NULL,
  `banType` tinytext NOT NULL,
  `banData` tinytext NOT NULL,
  `banStatus` tinyint(1) NOT NULL DEFAULT '1',
   PRIMARY KEY (`banID`)
);
