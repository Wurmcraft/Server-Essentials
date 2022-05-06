CREATE TABLE transfers (
  transfer_id SERIAL NOT NULL,
  uuid text NOT NULL,
  start_time text NOT NULL,
  items text NOT NULL,
  server_id text NOT NULL,
  PRIMARY KEY (transfer_id)
);
