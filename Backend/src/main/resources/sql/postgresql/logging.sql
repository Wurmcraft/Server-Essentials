CREATE TABLE logging (
  server_id text NOT NULL,
  timestamp text NOT NULL,
  action_type text NOT NULL,
  action_data json NOT NULL,
  uuid text NOT NULL,
  x int NOT NULL,
  y int NOT NULL,
  z int NOT NULL,
  dim int NOT NULL
);
