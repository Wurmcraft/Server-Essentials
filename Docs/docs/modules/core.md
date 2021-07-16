# Core Module

The Core Module is a module designed to handle most of the management for the other modules, such as player data management or unloaded module defaults. This module will be loaded even if its not specified in the `enabledModules` in the global config.

Name: `Core`

Requirements: `None`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| `SE`        | `command.se`               | Get Information, Status or reload SE related data                           | `Server-Essentials`, `ServerEssentials` | Medium                |
| `Manage`    | `command.manage`           | Directly Interact wit SE's management system for status or force reloading  |                                         | High                  |

### Non-Command Permission

`None`

### Config

File: `Modules/Core.json`

| Name            | Description                                                                                                                                                             |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `defaultLang`   | Default language for players and console, See [Github](https://github.com/Wurmcraft/Server-Essentials/tree/dev/1.12.2-Forge/language) for a list of supported languages |
| `langStorageURL`| Location to download the updated language files, Adv. Feature, It requires an wget'able base URL with the keys with an .json on the end                                 |
