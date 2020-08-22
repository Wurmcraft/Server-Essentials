# Commands

## AutoRank Module
---
**/AR**

Create, Delete, Modify or Display a player's current rank-up status

Aliases:  `AutoRank`

Usage

  - `/ar check` Displays your current rank-up status

  - `/ar check "player"` Displays another player's rank-up status

  - `/ar create "currentRank" "nextRank" "timeInMinutes"` Create a new AutoRank with the specified values

  - `/ar create "currentRank" "nextRank" "timeInMinutes" "amountOfDefaultCurrency" "expLvls"` Create a new AutoRank with the specified values.

  - `/ar delete "rank"` Deletes a given AutoRank.

Permission

  - `autorank.ar` Allows a player to run the command.

  - `autorank.check` Allows a player to check there own AutoRank status.

  - `autorank.check.other` Allows a player to check another players AutoRank status.

  - `autorank.create` Allows creation a new AutoRank

  - `autorank.delete` Allows deletion of a AutoRank

---


## Ban Module
---
**/GlobalBan**

Ban a player globally within the network / connected servers

Aliases:  `GBan`

Usage

  - `/gban "player" "reason""` Bans the given player for the given reason across all servers


Permission

  - `ban.globalBan` Allows a staff member to ban a player across all connected servers

---

---
**/GlobalPardon**

Globally Pardon a player from the network

Aliases:  `GPardon`

Usage

  - `/gpardon "player""` Pardons a given player from the network


Permission

  - `ban.globalpardon` Allows a staff member to pardon a player across all connected servers

---

## Core Module
---
**/SE**

Basic Command to interact and gain information about Server Essentials.

Aliases:  `ServerEssentials`

Usage

  - `/se modules` Displays the current active modules.

  - `/se version` Displays the current Server Essentials version.

  - `/se storage` Displays the current Server Essentials storage scheme.

  - `/se reload "module"` Reloads the selected module from Server Essentials

Permission

  - `core.se` Enabled the ability to get information about Server Essentials

  - `core.se.reload` Enables the ability to reload a module within Server Essentials

---


## Discord Module
---
**/Verify**

Verify a user via the [SE-Bot]()

Aliases:  `VerifyCode`

Usage

  - `/verify "code"` Verify a minecraft account via the code given from the [SE-Bot]()

Permission

  - `discord.verify` Allows a player to verify there IGN with the database

---

## Economy Module
---
**/Balance**

Displays a users currency

Aliases:  `Bal, B, Money, M`

Usage

  - `/bal` Displays the user's currency

  - `/bal  "player"` Display another player's currency

Permission

  - `economy.balance` Enabled the ability to get information about Server Essentials

  - `economy.balance.other` Allows for checking of another's balance

---

---
**/Pay**

Displays a users currency

Aliases:  `SendMoney, PayPal`

Usage

  - `/pay "player" "amount"` Send another player a amount of the default currency

  - `/pay "player" "amount" "name"` Send another player a amount of the specified currency

Permission

  - `economy.pay` Allows sending money from one player to another

---

---
**/Eco**

Displays a users currency

Aliases:  `SendMoney, PayPal`

Usage

  - `/eco "player" <add, rem> "amount"` Add or Remove the default currency from the given player

  - `/eco "player" <add, rem> "amount" "name"` Add or Remove the specified currency from the given player

Permission

  - `economy.eco` Allows for the management of a players currency

---


## General Module
---
**/Gamemode**

Changes a users gamemode

Aliases:  `GM, GMC, GMCA, GMS, GMSP, C, A, S, SP`

Usage

  - `/gm "mode"` Changes a players current gamemode to the one specified

  - `/gm "player" "mode"` Changes another players current gamemode to the one specified

Permission

  - `general.gamemode` Enables use of the gamemode Command

  - `general.gamemode.creative` Enables the ability to change the gamemode to creative

  - `general.gamemode.adventure` Enables the ability to change the gamemode to adventure

  - `general.gamemode.survival` Enables the ability to change the gamemode to survival

  - `general.gamemode.spectator` Enables the ability to change the gamemode to spectator

  - `general.gamemode.other` Enables the ability to change another players gamemode

---

---
**/Burn**

Lights another player on fire for a given amount of time

Aliases:  `Ignite`

Usage

  - `/burn "player"` Light a player on fire for 1 second

  - `/burn "player" "amount"` Light a player on fire for X amount of second

Permission

  - `general.burn` Allows for the management of a players currency

---

---
**/ChunkLoading**

Gives a list of all the currently chunk-loaded chunks

Aliases:  ``

Usage

  - `/chunkloading` Displays a list of all the chunk-loaded chunks

Permission

  - `general.chunkloading` Allows for the listing of chunk-loaded chunks

---

---
**/DeletePlayerFile**

Deletes a given player's player-file

Aliases:  `DPF`

Usage

  - `/DPF "player"` If the player is online it will delete the player's player-file

  - `/DPF "uuid"` If the specified player is offline the uuid is required to delete the player's player-file

Permission

  - `general.dpf` Allows for the deletion of a player's playerfile

---

---
**/Freeze**

Keeps another player from moving

Aliases:  `Bubble`

Usage

  - `/Freeze "player"` Keeps the given player from moving until the command is run again

Permission

  - `general.freeze` Allows for the freezing of another player

---

---
**/Invsee**

Keeps another player from moving

Aliases:  ``

Usage

  - `/invsee "player"` Displays the given players inventory

Permission

  - `general.invsee` Allows viewing of another players inventory

  - `general.invsee.modify` Allows modifying of another's inventory

---

---
**/Smite**

Spawn lightning on-top of another player

Aliases:  `Lightning`

Usage

  - `/smite "player"` Smites another player

Permission

  - `general.smite` Allows smiting of another player

---

---
**/Sudo**

Force another player to run a command, within there current permission

Aliases:  `ForceRun`

Usage

  - `/sudo "player" "command"` Force another to run the given command

Permission

  - `general.sudo` Allows for force-running a command for another

---

---
**/UUID**

Displays a players UUID

Aliases:  ``

Usage

  - `/uuid "player"` Displays a users UUID

Permission

  - `general.uuid` Allows for the display of another's UUID

---

---
**/Vanish**

Toggles the ability of others to view your player.

!!! note "Information"
    The other players can still see things being opened, modified or otherwise interactions that you have done.

Aliases:  ``

Usage

  - `/vanish` Makes you vanish and reappear to the players on the server

Permission

  - `general.vanish` Allows for the use of the vanish command

---

---
**/Seen**

Displays when a player was last found on the server / network.

Aliases:  `LastSeen`

Usage

  - `/lastSeen "player"` Displays the last time a player was online

Permission

  - `general.lastseen` Allows for the use of the seen command

---

---
**/List**

Displays the players currently online on the server / network.

Aliases:  `OnlinePlayers`

Usage

  - `/list` Displays the online players seperated by the server's ID

Permission

  - `general.list` Allows for the use of the list command

---

---
**/MOTD**

Displays the message of the day

Aliases:  ``

Usage

  - `/motd` Displays the MOTD

  - `/motd "lineNo" "Message"` Change the current lineNo to another message

Permission

  - `general.motd` Allows for the displaying of the MOTD

  - `general.motd.modify` Allows for modification of the MOTD

---

---
**/Rules**

Displays the rules of the server

Aliases:  ``

Usage

  - `/rules` Displays the Rules

  - `/rules "lineNo" "Message"` Change the current lineNo to another message

Permission

  - `general.rules` Allows for the displaying of the Rules

  - `general.rules.modify` Allows for modification of the Rules

---

---
**/onlineTime**

Displays how long the player has played on the server / network.

Aliases:  `Time`

Usage

  - `/onlineTime "player"` Displays how long the given player has been playing on the server / network

Permission

  - `general.onlineTime` Allows for the use of the OnlineTime command

---

---
**/Broadcast**

Send a message to everyone on the server without a prefix

Aliases:  `BC`

Usage

  - `/bc "Message"` Send a message to everyone on the server with the given formatting

Permission

  - `general.broadcast` Allows for broadcasting a message

---

---
**/RandomMessages**

Sends a random message to everyone on the server from a preset list of messages

Aliases:  `Rm`

Usage

  - `/Rm` Sends a random message to everyone on the server

  - `/Rm "lineNo" "Message"` Change the current lineNo to another message

Permission

  - `general.randomMessages` Allows for sending a random message to everyone on the server

  - `general.randomMessages.modify` Allows for modification of the Random Messages Preset

---

---
**/Say**

Send a message to everyone on the server with the prefix [Server]

Aliases:  ``

Usage

  - `/say "Message"` Send a message to everyone on the server with the given formatting

Permission

  - `general.say` Allows for broadcasting a message with a prefix

---

---
**/EChest**

Open a Ender Chest for the given player

Aliases:  `EnderChest`

Usage

  - `/echest` Open your ender chest

  - `/echest "player"` Open the given players ender chest

Permission

  - `general.echest` Allows for opening of your personal ender chest

  - `general.echest.other` Allows for opening of another's ender chest

  - `general.echest.modify` Allows for modifying another players ender chest

---

---
**/Feed**

Fills up the given players hunger bar

Aliases:  ``

Usage

  - `/feed` Feed yourself

  - `/feed "player"` Feed another player

Permission

  - `general.feed` Allows for feeding yourself

  - `general.feed.other` Allows for the feeding of other players

---

---
**/Fly**

Allows for toggling flight for the given player

Aliases:  ``

Usage

  - `/fly` Toggle flight for yourself

  - `/fly "player"` Toggle flight for others

Permission

  - `general.fly` Allows for toggling flight for yourself

  - `general.fly.other`  Allows for toggling flight for others

---

---
**/God**

Allows for toggling god-mode for the given player

Aliases:  ``

Usage

  - `/god` Toggle god-mode for yourself

  - `/god "player"` Toggle god-mode for others

Permission

  - `general.god` Allows for toggling god-mode for yourself

  - `general.god.other`  Allows for toggling god-mode for others

---

---
**/Hat**

Allows for forcing different items on the players head

Aliases:  ``

Usage

  - `/hat` Place the current held item on your head

Permission

  - `general.hat` Allows for placing of invalid items on the players head

---

---
**/Heal**

Set a player's health to max

Aliases:  ``

Usage

  - `/heal` Heals yourself

  - `/heal "player"` Heals the specified player

Permission

  - `general.heal` Allows for healing of themselves

  - `general.heal.other` Allows for healing of others

---


---
**/Rename**

Changes the held items name to the given one

Aliases:  `Name`

Usage

  - `/rename "Name"` Changes the current held item's name to the given one

Permission

  - `general.rename` Allows for changing of a item's name without the use of an anvil

---

---
**/Skull**

Creates a skull of the given name

Aliases:  ``

Usage

  - `/skull "Name"` Gives the player a skull with the given name

Permission

  - `general.skull` Allows for the creation of custom skulls

---


---
**/Speed**

Allows for changing of the given players walk or fly speed

Aliases:  ``

Usage

- `/speed "amount"` Changed both speeds to the given amount

- `/speed "player" "amount"` Changed both speeds to the given amount for the given player

- `/speed <fly, walk, both> "amount"` Changed the speed of the given type to the given amount

- `/speed <fly, walk, both> "player" "amount"` Changed the speed of the given type to the given amount for the given player

Permission

  - `general.speed` Set of speed of yourself

  - `general.speed.other` Set of speed of another player

---

---
**/Back**

Teleport to your last known location

Aliases:  ``

Usage

  - `/back` Teleport back to the last known location

Permission

  - `general.back` Allows for the usage of the back command

  - `general.back.death` Allows for the usage of the back command on death

---

---
**/DelHome**

Deletes the location of the given home

Aliases:  `DeleteHome, DHome`

Usage

  - `/delHome "name"` Deletes the given home

Permission

  - `general.delHome` Allows for the deletion of a home

---


---
**/Home**

Teleport to the location of a home

Aliases:  `H`

Usage

  - `/home` Teleport to the location of the default home

  - `/home "name"` Teleport to the given home

Permission

  - `general.home` Allows for the teleportation to a given home

---

---
**/Jump**

Teleport to the location of your cursor

Aliases:  ``

Usage

  - `/jump` Teleport's you to the location of your cursor

Permission

  - `general.jump` Allows for the teleportation to the cursor location

---

---
**/SetHome**

Create a new home based on your current location

Aliases:  `SetH, Sh`

Usage

  - `/setHome` Create a default home

  - `/setHome "name"` Create a home at the current location with the given name

Permission

  - `general.sethome` Allows for the creation of a home

---

---
**/SetSpawn**

Sets the world spawn at the current location

Aliases:  ``

Usage

  - `/SetSpawn` Sets the current location of spawn for all ranks

  - `/SetSpawn "rank"` Sets the current location of spawn for the given rank

  - `/SetSpawn "firstJoin"` Sets the current location of spawn for new players only

Permission

  - `general.setSpawn` Sets the location of spawn

---

---
**/Spawn**

Teleport to the location of the world's spawn

Aliases:  ``

Usage

  - `/spawn` Teleport to the location of the world's spawn

Permission

  - `general.spawn` Sets the location of spawn

---

---
**/SetWarp**

Sets your current location as a warp

Aliases:  ``

Usage

  - `/setWarp "name"` Sets the current location of spawn for the specified warp

Permission

  - `general.setWarp` Sets the location of a warp

---

---
**/Warp**

Teleport to the location of a warp

Aliases:  `W`

Usage

  - `/warp "name"` Teleport to a given warp

Permission

  - `general.warp.<name>` Teleport to the given warp, were "name" is the name of a given warp

---

---
**/TPAAccept**

Accept a request to teleport

Aliases:  `tpAccept`

Usage

  - `/tpaccept` Accept a active TPA Request

Permission

  - `general.tpaAccept` Allows the player to accept a tpa request

---

---
**/TPA**

Request to teleport to another player

Aliases:  ``

Usage

  - `/tpa "player"` Request to teleport to another player

Permission

  - `general.tpa` Allows for sending of tpa requests

---

---
**/TPAll**

Request to teleport to another player

Aliases:  ``

Usage

  - `/tpAll` Force Teleports everyone on the server to your current location

  - `/tpAll "rank"` Force Teleports everyone on the server with the given rank to your current location

Permission

  - `general.tpall` Allows for the teleportation of everyone of the given server to your current location

---

---
**/TP**

Teleport to a given location

Aliases:  ``

Usage

- `/tp "x" "y" "z"` Teleport to the given cords in the same dimension

- `/tp "x" "y" "z" "dim"` Teleport to the given cords in the specified dimension

- `/tp "x" "z"` Teleport to the given cords with y adjustment

- `/tp "player" "x" "y" "z"` Teleport to the given cords in the same dimension

- `/tp "player" "x" "y" "z" "dim"` Teleport to the given cords in the specified dimension

- `/tp "player" "x" "z"` Teleport to the given cords with y adjustment

Permission

  - `general.tp.cords` Allows for teleportation to coordinates only

  - `general.tp.cords_other` Allows for teleportation of other players to coordinates only

  - `general.tp.player` Allows for the teleportation of other players

---

---
**/TPOffline**

Teleport to the location of a offline player

Aliases:  `TpOff`

Usage

- `/toOffline "player"` Teleport to the logout location of the given player

Permission

  - `general.tooffline` Allows for teleportation to offline players


---

---
**/Rain**

Makes it rain the current world

Aliases:  ``

Usage

- `/rain` Makes it rain in the current world

Permission

  - `general.rain` Allows for spawning of rain clouds

---

---
**/Sun**

Makes it sunny in the current world

Aliases:  ``

Usage

- `/sun` Makes it sunny in the current world

Permission

  - `general.sun` Allows for the destruction of rain clouds

---

---
**/Thunder**

Spawns a thunder storm in the current world

Aliases:  ``

Usage

- `/thunder` Spawns a thunder storm in the current world

Permission

  - `general.thunder` Allows for spawning of a thunderstorm

---

---
**/Ping**

Sends a message back with your current ping

Aliases:  ``

Usage

- `/ping` Send a message back with your current ping

Permission

  - `general.ping` Allows for the player to run the ping command

---


## Language Module
---
**/Channel**

Change your currently selected channel

Aliases:  `Ch`

Usage

  - `/ch "name"` Change to the specified channel

  - `/ch list` Display a list of all the channels

Permission

  - `language.channel` Allows for basic access to the /ch command

  - `language.channel.<name>` Allows changing to the specified channel, were <name> is replaced with the channel name

---

## Rank Module
---
**/Rank**

Allows for the creation, deletion or changing of a rank

Aliases:  ``

Usage

  - `/rank "player" "rank"` Changes the specified player into the given rank

  - `/rank "rank" prefix "prefix"` Changes a given ranks prefix

  - `/rank "rank" suffix "suffix"` Changes a given ranks suffix

  - `/rank "rank" <add, del> <permission, inheritance> <node>` Changes a given ranks permission or inheritance

  - `/rank list` Displays all the current ranks

  - `/rank <create, del> "rank"` Create or Delete's the given rank

Permission

  - `rank.change` Allows changing of a ranks settings

  - `rank.rank` Allows for changing of a given users rank

---

## Security Module
---
**/Lockdown**

Disables most interactions and placements while its enabled

Aliases:  ``

Usage

  - `/lockdown"` Toggles the current lockdown status

Permission

  - `security.lockdown` Allows the toggling of the lockdown

---

---
**/Mods**

Displays a list of all the mods of the current client

Aliases:  ``

Usage

  - `/mods "player""` Displays a list of the player's mods

Permission

  - `security.mods` Allows for checking of a players mods

---
