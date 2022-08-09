# Discord Module

The Discord module is a module designed to verify and link user accounts between the server and a discord server along with allowing chating between discord and the servers. It can also be used for specific commands to be run on the players behaf incase its neeed,

Example: `Delete player file`, `Send to spawn`, `Kill`

Name: `Discord`

Requirements: `Core`, `Must be on using` ['Rest'](/guides/rest) `as storage type along with configured bot for full functionallity`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| `Verify`    | `discord.verify`           | Verify a user using a verifing code, generated from the discord bot         | ``                                      | `low`                 |

### Non-Command Permission

| Description                                                                                                                                              | Permission Node    |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- |--------------------|

### Config

File: `Modules/Discord.json`

| Name              | Description                                                                                                                                                             |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `verifyCommands`  | A list of commands to run to run once a user has been verified                                                                                                          |
