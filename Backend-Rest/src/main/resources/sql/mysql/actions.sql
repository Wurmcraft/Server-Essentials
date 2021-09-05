CREATE TABLE `actions` (
  `relatedID` tinytext NOT NULL,
  `host` tinytext NOT NULL,
  `action` tinytext NOT NULL,
  `actionData` json NOT NULL,
  `timestamp` bigint(20) NOT NULL
);