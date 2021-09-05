CREATE TABLE `users` (
  `uuid` tinytext NOT NULL,
  `username` tinytext NOT NULL,
  `rank` text NOT NULL,
  `perms` text NOT NULL,
  `perks` text NOT NULL,
  `language` tinytext NOT NULL,
  `muted` tinyint(1) NOT NULL DEFAULT '0',
  `muteTime` tinytext NOT NULL,
  `displayName` tinytext NOT NULL,
  `discordID` text NOT NULL,
  `trackedTime` json NOT NULL,
  `wallet` json NOT NULL,
  `rewardPoints` smallint(6) NOT NULL,
  `passwordHash` text NOT NULL,
  `passwordSalt` tinytext NOT NULL,
  `systemPerms` text NOT NULL,
  PRIMARY KEY (`uuid`(36))
);
