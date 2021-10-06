CREATE TABLE `transfers` (
  `transfer_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` tinytext NOT NULL,
  `start_time` tinytext NOT NULL,
  `items` json NOT NULL,
  `server_id` tinytext NOT NULL,
  PRIMARY KEY (`transfer_id`)
);