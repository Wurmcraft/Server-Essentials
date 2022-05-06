CREATE TABLE `autoranks` (
  `rank` tinytext NOT NULL,
  `next_rank` tinytext NOT NULL,
  `play_time` smallint(6) NOT NULL,
  `currency_name` tinytext NOT NULL,
  `currency_amount` int(11) NOT NULL,
  `special_events` json NOT NULL
);
