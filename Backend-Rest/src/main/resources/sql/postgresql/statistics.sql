CREATE TABLE statistics (
  server_id text NOT NULL,
  uuid text NOT NULL,
  timestamp text NOT NULL,
  event_type text NOT NULL,
  event_data json NOT NULL
);
