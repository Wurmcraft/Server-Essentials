CREATE TABLE bans (
  ban_id SERIAL NOT NULL,
  uuid text NOT NULL,
  ip text NOT NULL,
  discord_id text,
  banned_by text NOT NULL,
  banned_by_type text NOT NULL,
  ban_reason text NOT NULL,
  timestamp text NOT NULL,
  ban_type text NOT NULL,
  ban_data text NOT NULL,
  ban_status int NOT NULL DEFAULT '1',
  PRIMARY KEY (ban_id)
);
