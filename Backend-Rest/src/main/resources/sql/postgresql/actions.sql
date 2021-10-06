CREATE TABLE actions (
  related_id text NOT NULL,
  host text NOT NULL,
  action text NOT NULL,
  action_data json NOT NULL,
  timestamp int NOT NULL
);