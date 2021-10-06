CREATE TABLE `users` (
  `uuid` tinytext NOT NULL,
  `username` tinytext NOT NULL,
  `rank` text NOT NULL,
  `perms` text NOT NULL,
  `perks` text NOT NULL,
  `language` tinytext NOT NULL,
  `muted` tinyint(1) NOT NULL DEFAULT '0',
  `mute_time` tinytext NOT NULL,
  `display_name` tinytext NOT NULL,
  `discord_id` text NOT NULL,
  `tracked_time` json NOT NULL,
  `wallet` json NOT NULL,
  `reward_points` smallint(6) NOT NULL,
  `password_hash` text NOT NULL,
  `password_salt` tinytext NOT NULL,
  `systemPerms` text NOT NULL,
  PRIMARY KEY (`uuid`(36))
);
