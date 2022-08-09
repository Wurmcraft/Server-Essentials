# Economy Module

The Economy module is a module designed to provide in-game currency and ways of trading / exchanging items for said currency.

Name: `Economy`

Requirements: `Core`

### Commands

| Name        | Permission Node                            | Description                                                                                           | Default Aliases                         | Recommended Security  |
| ----------- | -------------------------------------------|-------------------------------------------------------------------------------------------------------|-----------------------------------------|-----------------------|
| Balance     | `economy.balance`, `economy.balance.other` | Allows for checking for a users currency (Sync'd between servers if using ['Rest'](/modules.rest))    | `bal`                                   | `Low`

### Non-Command Permission

| Description                                                                                                                                              | Permission Node    |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- |--------------------|

### Config

File: `Modules/Rank.json`

| Name                       | Description                                                                                                                                                             |
|----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `serverCurrency`           | Name of the primary currency used by the server                                                                                                                         |
| `rankTaxes`                | Amount of taxes a given user will pay in taxes `*` is used to set for all ranks                                                                                         |
| `commandTaxMultiplayer`    | Multipler for taxes that are related to commands                                                                                                                        |
| `payTaxMultiplayer`        | Multipler for taxes that are related to paying someone                                                                                                                  |
| `bankTaxMultiplayer`       | Multipler for taxes that are related to banking                                                                                                                         |
| `incomeTaxMultiplayer`     | Multipler for taxes that are related to income                                                                                                                          |
| `salesTaxMultiplayer`      | Multipler for taxes that are related to sales                                                                                                                           |
