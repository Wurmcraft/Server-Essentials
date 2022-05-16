# AutoRank Module

The Rank module is a module designed to manage users permission and group related players together into groups / ranks

Name: `Rank`

Requirements: `Core`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| `Autorank`  |   `autorank.autorank`, `autorank.autorank.create` | Check your status to rankup or create a new rankup instance | `ar`                             | `low` for check and `high` for create

### Non-Command Permission

| Description                                                                                                                                              | Permission Node    |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- |--------------------|

### Config

File: `Modules/Autorank.json`

| Name              | Description                                                                                                                                                             |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `announceRackup`  | Announce to other users when someone on the server rankup's                                                                                                             |
