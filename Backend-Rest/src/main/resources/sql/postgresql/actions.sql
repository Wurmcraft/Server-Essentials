CREATE TABLE actions (
  relatedID text NOT NULL,
  host text NOT NULL,
  action text NOT NULL,
  actionData json NOT NULL,
  timestamp int NOT NULL
);