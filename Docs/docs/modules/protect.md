# Chat Module

The Chat module is a module designed to manage and format chat via the use of channels.

Name: `Logging`

Requirements: `Core`

Note: This module does not allow claiming, but

### Commands

| Name | Permission Node | Description | Default Aliases | Recommended Security |
|------|-----------------|-------------|-----------------|----------------------|

### Config

File: `Modules/Claim.json`

| Name                      | Description                                              |
|---------------------------|----------------------------------------------------------|
| `defaultType`             | Default type of claiming to use                          |
| `defenseRange`            | Radius around claims to protect                          |
| `minClaimSize`            | Minimum size / area to claim in blocks                   |
| `preventNearbyExplosions` | Prevents explosions within the `defenseRange` of a claim |
| `claimNotify`             | Notify players when they enter a claimed area            |
| `trackingUpdateTimeTicks` | Time in ticks to check for player claim notify updates   |
