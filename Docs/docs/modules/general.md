# Rank Module

The General module is a module designed to add the basic commands used most commonly.

Name: `General`

Requirements: `Core`

### Commands

| Name             | Permission Node                                                                                     | Description                                                                 | Default Aliases               | Recommended Security |
|------------------|-----------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|-------------------------------|----------------------|
| Admin            | -                                                                                                   | -                                                                           | -                             | -                    |
| Break            | `command.break`, `command.break.multi`                                                              | Break a block you are looking at                                            |                               | Medium               |
| Burn             | `command.burn`                                                                                      | Sets a given person on fire                                                 | `Fire`, `Ignite`              | Low                  |
| ChunkLoading     | `command.chunkloading`                                                                              | Displays a list of chunkloading chunks                                      |                               | Medium               |
| Convert          | `command.convert`                                                                                   | Gives the items string to be used in configs                                |                               | Low                  |
| Day              | `command.day`                                                                                       | Set's the time to day, aka `1000`                                           |                               | Low                  |
| DeleteChunk      | `command.deletechunk`                                                                               | Delete's a chunk, command uses x,z pos not chunk cords, its auto converted. |                               | Low                  |
| DeletePlayerFile | `command.deleteplayerfile`                                                                          | Delete's the given player's player file, for minecraft                      | `DPF`                         | High                 |
| Dust             | `command.dust`                                                                                      | Set's the time to dust, aka `12000`                                         |                               | Low                  |
| Freeze           | `command.freeze`                                                                                    | Freezes a given player in place.                                            |                               | Medium               |
| Invsee           | `command.invsee`, `command.invsee.other`, `command.invseee.modify`                                  | Lets you look into another players inventory.                               | `ISee`                        | Medium               |
| Kill             | `command.kill`                                                                                      | Kills another player                                                        |                               | Medium               |
| Night            | `command.night`                                                                                     | Set's the time to night, aka `12000`                                        |                               | Medium               |
| Noon             | `command.noon`                                                                                      | Set's the time to noon, aka `6000`                                          |                               | Medium               |
| SendToSpawn      | `command.sendtospawn`                                                                               | Sends the given username or UUID to spawn. (Works if they are offline)      |                               | Medium               |
| Smite            | `command.smite`                                                                                     | Spawns lightning above the given player.                                    |                               | Low                  |
| Sudo             | `command.sudo`                                                                                      | Forces the given player to run a command. (Like you typed it in chat)       |                               | High                 |
| Time             | `command.time`                                                                                      | Set's the world time, based on the player's current world                   |                               | Low                  |
| Vanish           | `command.vanish`                                                                                    | Makes you invisible to most players.                                        |                               | Medium               |
| Gamemode         | -                                                                                                   | -                                                                           | -                             | -                    |
| Adventure        | `command.adventure`                                                                                 | Chance yours or another player gamemode to adventure                        | `GMA`                         | Medium               |
| Creative         | `command.creative`                                                                                  | Chance yours or another player gamemode to creative                         | `GMC`                         | High                 |
| Gamemode         | `command.gamemode`                                                                                  | Chance yours or another player gamemode                                     | `GM`                          | High                 |
| Spectator        | `command.spectator`                                                                                 | Chance yours or another player gamemode to spectator                        | `GMSP`                        | Medium               |
| Survival         | `command.survival`                                                                                  | Chance yours or another player gamemode to survival                         | `GMS`                         | Low                  |
| Item             | -                                                                                                   | -                                                                           | -                             | -                    |
| GiveItem         | `command.giveitem`                                                                                  | Get an item based on its string                                             | `Item`                        | High                 |
| Kit              | `command.kit`, `command.kit.create`, `command.kit.delete`, `command.kit.list`, `command.kit.{name}` | Create, delete or get a kit                                                 |                               | Medium               |
| Rename           | `command.rename`                                                                                    | Rename a given item, supports using & for colors                            | `NickItem`, `Name`            | Low                  |
| Misc             | -                                                                                                   | -                                                                           | -                             | -                    |
| AFK              | `command.afk`                                                                                       | Mark or unmark as AFK, note this is automatic aswell.                       | `AwayFromKeyboard`            | Low                  |
| RandomMessages   | `command.randommessages`, `command.randommessages.delete`, `command.randommessages.add`             | Displays a random message each time its used, from the list                 |                               | Low                  |
| Rules            | `command.rules`, `command.rules.delete`, `command.rules.add`                                        | Displays the rules, add or delete them aswell                               |                               | Medium               |
| Say              | `command.say`                                                                                       | Send a message to everyone on the same server                               |                               | Medium               |
| Seen             | `command.seen`                                                                                      | Displays the last time a player was "seen", works across servers            |                               | Medium               |
| Perk             | -                                                                                                   | -                                                                           | -                             | -                    |
| EnderChest       | `command.enderchest`, `command.enderchest.other`                                                    | Allows opening a ender chest for you or a given player                      | `EChest`                      | Medium               |
| Feed             | `command.feed`, `command.feed.other`                                                                | Gives the given player food                                                 | `ImTooLazyToEatSoYouDoIt`     | Low                  |
| Fly              | `command.fly`, `command.fly.other`                                                                  | Allows the given player to fly                                              | `ImABird`, `Bird`             | Medium               |
| God              | `command.god`, `command.god.other`                                                                  | Allows the given player to be immune to most damage                         | `Invincible`                  | Medium               |
| Hat              | `command.hat`                                                                                       | Puts the held item in that helmet / hat slot, even if its not possible      |                               | Low                  |
| Heal             | `command.heal`, `command.heal.other`                                                                | Sets the player to max health                                               |                               | Low                  |
| Repair           | `command.repair`                                                                                    | Sets the held item to 0 damage, note this may mess up some items.           |                               | Low                  |
| Skull            | `command.skull`                                                                                     | Gives a skull for the given user                                            | `Head`                        | Low                  |
| Speed            | `command.speed`                                                                                     | Set the walking and flying speed, high values will cause issues             | `Movement`                    | Medium               |
| Vault            | `command.vault`                                                                                     | Allows a given player to access there vault                                 | `Storage`                     | Medium               |
| Player           | -                                                                                                   | -                                                                           | -                             | -                    |
| Experience       | `command.experience`                                                                                | Give Exp to the given player                                                | `Exp`                         | Low                  |
| List             | `command.list`                                                                                      | List given players on the server aswell as linked ones                      | `Players`, `L`                | Low                  |
| Playtime         | `command.playtime`                                                                                  | Display's a user's total playtime and server play time                      | `Time`, `OnlineTime`          | Low                  |
| Suicide          | `command.suicide`                                                                                   | Kills youself                                                               | `Seppuku`                     | Low                  |
| TrashCan         | `command.trashcan`                                                                                  | Opens a inventory that delete's item on closing                             | `Trash`, `Can`, `DestroyItem` | Low                  |
| Whois            | `command.whois`                                                                                     | Gives infomation about a given username or nickname                         | `Who`                         | Low                  |
| Teleport         | -                                                                                                   | -                                                                           | -                             | -                    |
| Back             | `command.back`                                                                                      | Teleports you to the last known location                                    | `B`                           | Low                  |
| DeleteWarp       | `command.deletewarp`                                                                                | Delete a warp                                                               | `DelWarp`, `DWarp`            | Medium               |
| DeleteHome       | `command.delhome`                                                                                   | Delete a home                                                               |                               | Medium               |
| Home             | `command.home`, `command.home.other`                                                                | Teleport to a set home point                                                | `H`,                          | Low                  |
| Jump             | `command.jump`                                                                                      | Teleport to where you are looking                                           |                               | Low                  |
| RTP              | `command.rtp`                                                                                       | Teleport to a random location, based on the configured settings             |                               | Low                  |
| SetHome          | `command.sethome`, `command.sethome.other`                                                          | Set the location for later teleportation                                    | `SHome`, `SH`                 | Low                  |
| SetSpawn         | `command.setspawn`                                                                                  | Set the global spawn or only one for a specific rank                        |                               | High                 |
| SetWarp          | `command.setwarp`                                                                                   | Create a new warp location                                                  |                               | Medium               |
| Spawn            | `command.spawn`                                                                                     | Teleport to spawn                                                           |                               | Medium               |
| Top              | `command.top`                                                                                       | Teleport to the highest Y point and or 256 blocks                           |                               | Low                  |
| TPAAccept        | `command.tpaccept`                                                                                  | Used to accept an TP Request                                                |                               | Low                  |
| TPA              | `command.tpa`                                                                                       | Request to teleport to someone else                                         |                               | Low                  |
| TPADeny          | `command.tpdeny`                                                                                    | Deny a tpa request                                                          |                               | Low                  |
| TPAHere          | `command.tpahere`                                                                                   | Request someone to teleport to you.                                         |                               | Low                  |
| TP               | `command.tp`                                                                                        | Teleport to another player                                                  |                               | Low                  |
| TPHere           | `command.tphere`                                                                                    | Teleport another player to your location (forcefully)                       |                               | Medium               |
| TpOffline        | `command.tpoffline`                                                                                 | Teleport to the location of an offline player                               | `OfflineTP`, `Offtp`, `Otp`   | Medium               |
| TpPos            | `command.tppos`                                                                                     | Teleport to a given position or dimension                                   | `tpp`                         | Low                  |
| Warp             | `command.warp`, `command.warp.{name}`                                                               | Teleport to a given warp                                                    | `tpp`                         | Low                  |
| Utils            | -                                                                                                   | -                                                                           | -                             | -                    |
| Ping             | `command.ping`                                                                                      | Used to check if the server command are working                             | `Alive`                       | Low                  |
| Tag              | `command.tag.add`, `command.tag.remove`, `command.tag.list`                                         | Adds or remove's tags from a given player                                   |                               | Medium               |
| UUID             | `command.uuid`                                                                                      | Lookup a users uuid, based on what you are able to provide, username, nick  | `ID`, `LookupUUID`            | Low                  |
| Weather          | -                                                                                                   | -                                                                           | -                             | -                    |
| Rain             | `command.rain`                                                                                      | Causes rain in the selected world                                           |                               | Low                  |
| Storm            | `command.storm`                                                                                     | Causes a storm in the selected world                                        |                               | Low                  |
| Sun              | `command.sun`                                                                                       | Causes the selected world to be sunny                                       |                               | Low                  |
| Weather          | `command.weather`                                                                                   | Changes the weather of the selected world                                   |                               | Low                  |

### Non-Command Permission

| Description                          | Permission Node      |
|--------------------------------------|----------------------|
| Allows teleporting to where you died | `general.back.death` |

### Perks

| Description                        | Permission Node  |
|------------------------------------|------------------|
| Increases the max amount of homes  | `home.{amount}`  |
| Increases the max amount of vaults | `vault.{amount}` |

### Config

File: `Modules/General.json`

| Name                    | Description                                                                    |
|-------------------------|--------------------------------------------------------------------------------|
| `defaultHomeName`       | Name of the home to use as the default home when one is not specified          |
| `minHomes`              | The minimum amount of homes a user may have                                    |
| `maxHomes`              | The maximum amount of homes a user may have                                    |
| `spawn`                 | Used to store the location of the spawns                                       |
| `rtpRadius`             | Radius from spawn for RTP to attempt to teleport                               |
| `rtpBiomeBlacklist`     | List of blacklisted biomes to not teleport a user to.  IE avoid Oceans, Rivers |
| `rtpDimensionWhitelist` | List of dimensions RTP will work in.                                           |
| `defaultVaultName`      | Name of the default player vault                                               |
| `afkCheckTimer`         | How often to check for AFK players                                             |
| `afkTimer`              | How long before a player triggers being AFK.                                   |
| `playTimeSync`          | How often playtime is saved.                                                   |
| `statusSync`            | How often server status is updated.                                            |
| `spawnAtHome`           | Do players spawn at there default home location?                               |


