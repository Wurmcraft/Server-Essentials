# Ban Module

The Ban Module is a module designed to link the servers together with a network wide ban system.

Name: `Ban`

Requirements: `Rest mode only`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| `ban`       | `command.ban`              | Base Ban command                                                            | `GlobalBan`                             | Low / High            |
| `ban.lookup`| `command.ban.lookup`       | Lookup a specific users bans                                                | `GlobalBan`                             | Low                   |
| `ban.create`| `command.ban.create`       | Create a ban for a specific user                                            | `GlobalBan`                             | High                  |
| `ban.delete`| `command.ban.delete`       | Delete a ban for a specific user                                            | `GlobalBan`                             | High                  |

### Non-Command Permission

`None`

### Config

File: `Modules/Ban.json`

| Name            | Description                                                                                                                                                             |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `defaultLang`   | Default language for players and console, See [Github](https://github.com/Wurmcraft/Server-Essentials/tree/dev/1.12.2-Forge/language) for a list of supported languages |
