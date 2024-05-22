# Transfer Module

The transfer module is a module designed to allow transferring of items between servers

Name: `Transfer`

Requirements: `Core`

Note: This module does not currently do anything.

### Commands

| Name | Permission Node | Description | Default Aliases | Recommended Security |
|------|-----------------|-------------|-----------------|----------------------|

### Config

File: `Modules/Transfer.json`

| Name                  | Description                                                         |
|-----------------------|---------------------------------------------------------------------|
| `transferID`          | ID of network for item transfers                                    |
| `itemBlacklist`       | list of items forbidden from being transfered.                      |
| `costToTransferItem`  | cost to transfer a item.                                            |
| `allowLargeTransfers` | Allow large transfers of items, aka larger than a hole chest / page |
