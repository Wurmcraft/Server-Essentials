CREATE TABLE `statistics` (
  `server_id` tinytext NOT NULL,
  `uuid` tinytext NOT NULL,
  `timestamp` tinytext NOT NULL,
  `event_type` tinytext NOT NULL,
  `event_data` json NOT NULL
);
