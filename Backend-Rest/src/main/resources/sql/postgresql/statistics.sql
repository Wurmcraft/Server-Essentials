CREATE TABLE statistics (
  serverID text NOT NULL,
  uuid text NOT NULL,
  timestamp text NOT NULL,
  eventType text NOT NULL,
  eventData json NOT NULL
);
