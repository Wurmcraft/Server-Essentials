CREATE TABLE `ranks` (
  `name` tinytext NOT NULL,
  `permissions` text NOT NULL,
  `inheritance` text NOT NULL,
  `prefix` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `prefix_priority` smallint(4) NOT NULL DEFAULT '0',
  `suffix` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `suffix_priority` smallint(4) NOT NULL DEFAULT '0',
  `color` text,
  `color_priority` tinyint(4) NOT NULL DEFAULT '0'
);
