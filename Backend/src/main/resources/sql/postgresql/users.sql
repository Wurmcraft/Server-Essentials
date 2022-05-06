CREATE TABLE users (
  uuid text NOT NULL,
  username text NOT NULL,
  rank text NOT NULL,
  perms text NOT NULL,
  perks text NOT NULL,
  lang text NOT NULL,
  muted boolean NOT NULL DEFAULT '0',
  mute_time text NOT NULL,
  display_name text NOT NULL,
  discord_id text NOT NULL,
  tracked_time json NOT NULL,
  wallet json NOT NULL,
  reward_points int NOT NULL,
  password_hash text NOT NULL,
  password_salt text NOT NULL,
  system_perms text NOT NULL,
  PRIMARY KEY (uuid)
);
