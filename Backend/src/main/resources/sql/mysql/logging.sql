CREATE TABLE `logging` (
  `server_id` tinytext NOT NULL,
  `timestamp` tinytext NOT NULL,
  `action_type` tinytext NOT NULL,
  `action_data` json NOT NULL,
  `uuid` tinytext NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `dim` smallint(6) NOT NULL
);
