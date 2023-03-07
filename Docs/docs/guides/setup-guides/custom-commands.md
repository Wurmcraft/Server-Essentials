# Custom Commands

These are simple .json files that are loaded upon the server starting, simular to how you can modify any of the commands, these just have a few extra features that allow for the creation for simple commands.

See the `Server-Essentials/Commands/Custom` folder for the current custom commands.

The example included is a `/website` command which simple sends a message with a link to the Server Essentials wiki and github.

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

The only section that is different from a standard command is the `functions` section which is used to determine what the command will do.
See the [Getting Started](../getting-started) for more information about the command format.

The `functions` is an array of things that command will do when it is run in-game.

The possible types of `type` for the functions is

- `MESSAGE` is used to send a message to the player running the command.
- `COMMAND` is able to force the player to run another command, if they have permission
- `CONSOLE_COMMAND` is used to run a command as it where being run by the console ignoring the players permissions or even if said player had ran that command.

Due to `functions` being an array you are able to stack these different functions to create unique effects or actions, however if you need something else, feel free to request a new feature on our [discord](https://discord.gg/jMHgCAY)
