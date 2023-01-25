# Getting Started

## Installing

Installing Server Essentials is simple and easy, you simple place the mod into the mods folder. to download sever essentials see one of the links below.

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/server-essentials)

[Latest](https://ci.wurmatron.io/job/Server%20Essentials/)

After placing the mod in the mods folder, simple start the server and it will generate the files required to configure / setup the mod.

## Basic Configuration

All files related to Server Essentials are stored in the root of the server files at `Server-Essentials/`, there are multiple folders within that hold categories of config's and player files.

### Commands (`Server-Essentials/Commands`)

This folder holds a list of all command config's along with the `Custom` folder which holds all the server's custom commands.

The command configuration file such as `adventure.json`

```json
{
  "name": "adventure",
  "enabled": true,
  "permissionNode": "command.adventure",
  "minRank": "",
  "secure": false,
  "aliases": [
    "Gma"
  ],
  "rankCooldown": {},
  "rankDelay": {},
  "currencyCost": {}
}
```
This command config allows the server owner to modify certain information about the command.

- `name` is used to signify how name of the name, this should not be changed, unless its a custom command
- `enabled` is used to determine if the command is able to be used / run at the given time.
- `permissionNode` is the permission node for the given command, while it can be changed its unrecommended due to it being able to get confusing quite easily.
- `minRank` is the minimal rank that is allowed to run this command. This is done though the inheritance system.
- `secure` is only used by the [`Security`](../../modules/security/) module, which requires the user to be on the `trusted-list` to be able to run this command.
- `aliases` is a list of aliases or alternate names that can be used to run this command.
- `rankCooldown` is a sub section that allows for the cooldown for a given based on a users rank. See below for examples
- `rankDelay` is a sub section that allows for the delay for a given based on a users rank. See below for examples
- `currencyCost` is a sub section that allows for requirement of paying to run the command. See below for examples

Rank Cooldown (`rankCooldown`) or Rank Delay (`rankDelay`)

```json
  {
    "*": 5,
    "Admin": 1
  }
```

Currency Cost (`currencyCost`)

```json
  {
    "currency": 50.0
  }
```

The number is allowed to have decimals (floating-point)

### Custom Commands (`Server-Essentials/Commands/Custom`)

Custom commands are a way for the server owner to create custom commands to do simple tasks.

```json
{
  "name": "Website",
  "aliases": [
    "Web",
    "Site"
  ],
  "functions": [
    {
      "type": "MESSAGE",
      "values": [
        "https://wiki.wurmatron.io/serveressentials",
        "https://github.com/Wurmcraft/Server-Essentials"
      ]
    }
  ],
  "minRank": "",
  "permissionNode": "customcommand.website",
  "secure": false,
  "rankCooldown": [],
  "rankDelay": [],
  "currencyCost": [],
  "canConsoleRun": true
}
```

- `name` is the name of the command, along with it being how to run the command such as `/Website`
- `aliases` is a list of aliases or alternate names that can be used to run this command.
- `functions` is used to setup how the command is run, see below for examples.
- `minRank` is the minimal rank that is allowed to run this command. This is done though the inheritance system.
- `permissionNode` is the permission node for the given command, while it can be changed its unrecommended due to it being able to get confusing quite easily.
- `secure` is only used by the [`Security`](../../modules/security/) module, which requires the user to be on the `trusted-list` to be able to run this command.
- `aliases` is a list of aliases or alternate names that can be used to run this command.
- `rankCooldown` is a sub section that allows for the cooldown for a given based on a users rank. See below for examples
- `rankDelay` is a sub section that allows for the delay for a given based on a users rank. See below for examples
- `currencyCost` is a sub section that allows for requirement of paying to run the command. See below for examples

Most of the values match the same as the standard config, and work the same.

Functions

There are three different types of functions, which can be stacked.

- `COMMAND` is used to simple run another command, the user has to have permission to run.
- `MESSAGE` is used to send messages to the player.
- `CONSOLE_COMMAND` is similar to `COMMAND` however the user is not required to have permission to run this command, its run via the server so command output is not displayed to the user

Messages will have `&` replaced with `§`, so it can be used to color your messages, see [Formatting Codes](https://minecraft.fandom.com/wiki/Formatting_codes) for codes.

### Modules (`Server-Essentials/Modules`)

The modules folder contains all the config's for all the modules, including the ones that you are not using. Each module configuration is different based on what each module does ,see the `Modules` Bar for a full list of modules along with config explanations.

### Storage (`Server-Essentials/Storage`)

This folder is used to store data related to the mod such as Ranks, Rankups, Channels and much more. While all of these can be modified, only a few are designed to be editable, such as `Ranks`, `AutoRank`, `Channel`, See specific tutorials such as [Creating a rank](../setup-guides/rank-creation) under the [Setup Guides](../../setup-guides)

## Global (`Server-Essentials/global.json`)

This is a configuration file designed to affect how Server Essentials functions, such as the loaded modules and such.

```json
{
  "general": {
    "debug": false,
    "serverID": "Not-Set"
  },
  "storage": {
    "storageType": "File",
    "token": "",
    "key": "",
    "baseURL": "https://localhost:8080/"
  },
  "performance": {
    "maxThreads": 4,
    "playerCacheTimeout": 300,
    "playerSyncInterval": 90,
    "useWebsocket": false,
    "dataloaderInterval": 5
  },
  "enabledModules": [
    "General",
    "AutoRank",
    "Chat",
    "Economy",
    "Rank"
  ],
  "configVersion": "654e807b"
}
```

The main things in the file you need to know about are `enabledModules` and `serverID`

- `enabledModules` is a list of the modules that Server Essentials will load upon starting up the server, for a list of Modules see the `Modules` Bar for a full list of modules along with config explanations.
- `serverID` is used to name the server, while this not necessary its highly recommended, due to it being difficult to change once the server has been running for a bit.
