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

  - `autorank.check` Allows a player to check there own AutoRank status.

  - `autorank.check.other` Allows a player to check another players AutoRank status.

  - `autorank.create` Allows creation a new AutoRank

  - `autorank.delete` Allows deletion of a AutoRank

---

## Discord Module
---
**/Verify**

Verify a user via the [SE-Bot]()

Aliases:  `VerifyCode`

Usage

  - `/verify "code` Verify a minecraft account via the code given from the [SE-Bot]()

Permission

  - `discord.verify` Allows a player to verify there IGN with the database

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
