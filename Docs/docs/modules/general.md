# Rank Module

The General module is a module designed to add the basic commands used most commonly.

Name: `General`

Requirements: `Core`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| Home        | command.home               | Teleport to a home                                                          | `H`                                     | Low                   |
| Home        | command.home.other         | Teleport to the home of another                                             | `H`                                     | Medium                |
| SetHome     | command.sethome            | Set a new home                                                              |                                         | Low                   |
| Sethome     | command.sethome.other      | Set the home for another user                                               |                                         | Medium                |
| DelHome     | command.delhome            | Delete an existing home                                                     |                                         | Low                   |
| DelHome     | command.delhome.other      | Delete another users existing home                                          |                                         | Medium                |
| Tpa         | command.tpa                | Request to teleport to another user                                         |                                         | Low                   |
| TpaAccept   | command.tpaaccept          | Accept another players request to teleport to your location                 | `TpAccept`                              | Low                   |
| TpaDeny     | command.tpadeny            | Deny the request for another player to teleport to your location            | `TpDeny`                                | Low                   |
| Spawn       | command.spawn              | Teleport to your highest rank spawn                                         | `Spwn`, `S`                             | Low                   |
| Spawn       | command.spawn.<spawnName>  | Teleport you to a specific spawn (for a given rank / name)                  | `Spwn`, `S`                             | Low                   |
| SetSpawn    | command.setspawn           | Set a spawn for a specific rank                                             |                                         | Low                   |

### Non-Command Permission

| Description                                                                                                                                              | Permission Node    |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- |--------------------|

### Config

File: `Modules/General.json`

| Name              | Description                                                                                                                                                             |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `defaultHomeName` | Name of the home to use as the default home when one is not specified                                                                                                   |
| `minHomes`        | The minimum amount of homes a user may have                                                                                                                             |
| `maxHomes`        | The maximum amount of homes a user may have                                                                                                                             |
