CREATE TABLE `bans` (
  `ban_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` tinytext NOT NULL,
  `ip` text NOT NULL,
  `discord_id` text,
  `banned_by` tinytext NOT NULL,
  `banned_by_type` text NOT NULL,
  `ban_reason` text NOT NULL,
  `timestamp` tinytext NOT NULL,
  `ban_type` tinytext NOT NULL,
  `ban_data` tinytext NOT NULL,
  `ban_status` tinyint(1) NOT NULL DEFAULT '1',
   PRIMARY KEY (`banID`)
);
