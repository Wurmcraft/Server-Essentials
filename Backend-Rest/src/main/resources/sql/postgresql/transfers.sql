CREATE TABLE transfers (
  transferID SERIAL NOT NULL,
  uuid text NOT NULL,
  startTime text NOT NULL,
  items json NOT NULL,
  serverID text NOT NULL,
  PRIMARY KEY (transferID)
);
