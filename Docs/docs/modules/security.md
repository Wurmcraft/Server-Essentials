# Security Module

The Rank module is a module designed to protect the server from malicious actors, such as unauthorized players getting admin perks or joining with forbidden client mods.

Name: `Security`

Requirements: `Core`

### Commands

| Name        | Permission Node            | Description                                                                 | Default Aliases                         | Recommended Security  |
| ----------- | ---------------------------|-----------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| Lockdown    | command.lockdown           | Prevents players from interacting with things on the server                 |                                         | Medium                |

### Non-Command Permission

| Description                                                                                                                                              | Permission Node    |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- |--------------------|
| Used as a way to notify players when a alt / same IP account joins the server                                                                            | `security.alt.notify`|

### Config

File: `Modules/Security.json`

| Name              | Description                                                                                                                                                             |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `modBlacklist`    | List of mods the prevent a user from user on there client's (Note: This is not 100% full proof, bypass is possible)                                                     |
| `trustedList`     | Direct URL for a list of player account's to be on the trusted list, highest rank (above all ranks, perms / non-trusted users)                                          |
| `lockdownEnabled` | Used by the /lockdown command to determine if the server is in lockdown or not                                                                                          |
| `autoOP`          | Automatically op all trusted user's upon joining the server if they are not already                                                                                     |
| `checkAlt`        | Check player account connections to see if they have the same IP                                                                                                        |
