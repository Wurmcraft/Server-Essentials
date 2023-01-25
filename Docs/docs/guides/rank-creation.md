# Creating a Rank

## In-Game

### Creation
To create a basic rank ingame you run `rank create {name}` where `{name}` is replaced with its name (without formatting)

![Rank Created](../img/rank_created.png)

`rank create testing`

### Prefix / Suffix
To change how this change's name is displayed in chat, such as its prefix or suffix you run `rank modify {name} add {prefix or suffix} <your display name here>` where

 - `{name}` is replaced with the rank name you created in the previous step.

- `{prefix or suffix}` is replaced with one of the two, such as `prefix` or `suffix`

- `<your display name here>` is replaced with how you want the name to be displayed, you can use `&` instead of `§` for [color codes](https://minecraft.fandom.com/wiki/Formatting_codes)

![Prefix set](../img/prefix_set.png)

`/rank testing prefix &b[Testing]`

![Prefix Chat](../img/chat_prefix.png)

### Adding / Removing Permission's
To add or delete permissions to a rank, you run `rank modify {name} {add, rem} permission {node}` where

 - `{name}` is replaced with the rank name you created in the previous step.

- `{add, rem}` is replaced with one of the two, such as `add` or `rem`

- `{node}` is replaced with the permission node you wish to add to the rank, see modules command list, such as [General](../../modules/general/)

![Prefix Chat](../img/rank_modify.png)

`/rank modify testing add perm command.home`


## File

To create rank, first go to `Server-Essentials/Storage/Rank/` in the root of the server files, you will see a few files here, by default there is `default.json` and `admin.json` which are the default ranks.

- Start by copying one of the .json's and rename into the rank you are trying to create such as `testing.json`
Upon opening the file you will see, something similar,

### Example File

```
{
  "name": "default",
  "permissions": [
    "command.help",
    "command.home",
    "command.sethome",
    "command.tpa",
    "command.tpaccept",
    "command.tpadeny",
    "command.spawn"
  ],
  "inheritance": [],
  "prefix": "&8[&7Default&8]",
  "prefix_priority": 0,
  "suffix": "&7",
  "suffix_priority": 0,
  "color": "&3",
  "color_priority": 0
}
```

### Variable Explanation

You will want to replace the `name` `default` to the one your named the file.

Each section of this file contains the information about the new rank. The permission section is an array of permission nodes, which can be  seen based on a modules command list, such as [General](../../modules/general/)

inheritance is a list of ranks that are "below" this one, such as having all the permissions as the rank "below"

Prefix is the formatting that appears before the players name such as [Testing] such as the image below

![Prefix Chat](../img/chat_prefix.png)

Suffix is the formatting that appears after the players name but before a chat message

Color is the color of the rank along with the color of the rank's text in chat

X_priority's are the priority that a given prefix, suffix or color will be used by a player when they have multiple ranks, the rank with the highest priority is displayed.
