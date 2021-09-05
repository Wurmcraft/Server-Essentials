CREATE TABLE bans (
  banID SERIAL NOT NULL,
  uuid text NOT NULL,
  ip text NOT NULL,
  discordID text,
  bannedBy text NOT NULL,
  bannedByType text NOT NULL,
  banReason text NOT NULL,
  timestamp text NOT NULL,
  banType text NOT NULL,
  banData text NOT NULL,
  banStatus int NOT NULL DEFAULT '1',
  PRIMARY KEY (banID)
);
