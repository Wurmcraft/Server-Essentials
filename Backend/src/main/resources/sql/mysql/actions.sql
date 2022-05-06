CREATE TABLE `actions` (
  `related_id` tinytext NOT NULL,
  `host` tinytext NOT NULL,
  `action` tinytext NOT NULL,
  `action_data` json NOT NULL,
  `timestamp` bigint(20) NOT NULL
);