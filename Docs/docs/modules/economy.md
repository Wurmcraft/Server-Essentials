# Economy Module

The Economy module is a module designed to provide in-game currency and ways of trading / exchanging items for said
currency.

Name: `Economy`

Requirements: `Core`

### Commands

| Name    | Permission Node                            | Description                                                                                        | Default Aliases | Recommended Security |
|---------|--------------------------------------------|----------------------------------------------------------------------------------------------------|-----------------|----------------------|
| Balance | `command.balance`, `command.balance.other` | Allows for checking for a users currency (Sync'd between servers if using ['Rest'](/modules.rest)) | `bal`           | `Low`                
| Economy | `command.eco`                              | Allows admins to change users balance, Add, Remove                                                 | `eco`           | `Moderate/High`      
| Market  | `command.market`                           | Used to view the market's                                                                          | `shop`          | `Low`                
| Market  | `command.marketList`                       | Used to create market listings                                                                     | `mlist`         | `Low`                
| Pay     | `command.pay`                              | Send Money to another player                                                                       | `pay`           | `Low`                
| Perk    | `command.perk`                             | Purchase Perks, Commands, Homes, Basically whatever its set to do.                                 | `perks`         | `Low`                
| Taxes   | `command.tax`                              | Get info about the server's taxes.                                                                 | `tax`           | `Low`                

### Non-Command Permission

| Description | Permission Node |
|-------------|-----------------|

### Config

File: `Modules/Economy.json`

| Name                    | Description                                                    |
|-------------------------|----------------------------------------------------------------|
| `serverCurrency`        | Name of the primary currency used by the server                |
| `taxes`                 | Configure the tax rate for specific actions                    |
| `maxListingsPerCommand` | No idea what this does.                                        |
| `itemBlacklist`         | list of items that cannot be listed on the market              |
| `defaultMaxListings`    | max amount of listings a standard person can create by default |

