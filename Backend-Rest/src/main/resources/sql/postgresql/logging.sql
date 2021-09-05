CREATE TABLE logging (
  serverID text NOT NULL,
  timestamp text NOT NULL,
  actionType text NOT NULL,
  actionData json NOT NULL,
  uuid text NOT NULL,
  x int NOT NULL,
  y int NOT NULL,
  z int NOT NULL,
  dim int NOT NULL
);
