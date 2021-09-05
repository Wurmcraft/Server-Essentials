CREATE TABLE `currencys` (
  `currencyID` tinyint(4) NOT NULL AUTO_INCREMENT,
  `displayName` tinytext NOT NULL,
  `globalWorth` decimal(10,4) NOT NULL,
  `sellWorth` decimal(10,4) NOT NULL,
  `tax` decimal(10,2) NOT NULL,
   PRIMARY KEY (`currencyID`)
);
