CREATE TABLE users (
  uuid text NOT NULL,
  username text NOT NULL,
  rank text NOT NULL,
  perms text NOT NULL,
  perks text NOT NULL,
  language text NOT NULL,
  muted int NOT NULL DEFAULT '0',
  muteTime text NOT NULL,
  displayName text NOT NULL,
  discordID text NOT NULL,
  trackedTime json NOT NULL,
  wallet json NOT NULL,
  rewardPoints int NOT NULL,
  passwordHash text NOT NULL,
  passwordSalt text NOT NULL,
  systemPerms text NOT NULL,
  PRIMARY KEY (uuid)
);
