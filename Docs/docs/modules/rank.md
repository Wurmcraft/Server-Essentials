# Rank Module

The Rank module is a module designed to manage users permission and group related players together into groups / ranks

Name: `Rank`

Requirements: `Core`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| Rank        | `command.rank`             | Allows for creating/deleting ranks                                          |                                         | `Medium`              |
| Rank        | `command.perm`             | Allows for adding/delete or setting a user's rank                            |                                         | `Medium`              |

### Non-Command Permission

| Description                                                                                                                                              | Permission Node    |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- |--------------------|

### Config

File: `Modules/Rank.json`

| Name              | Description                                                                                                                                                             |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `defaultRank`     | The rank new users will be joined into upon there first join the server                                                                                                 |
