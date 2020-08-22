# Global.json

***Location:*** `Server-Essentials/Global.json`

 // Currently Loaded Modules, In this example there are `General` and `Rank` Modules loaded

  ***"modules":*** ["General","Rank"],   

  // Name of the server for use with the database or the `Track` module

  ***"serverID":*** "Server-Name",

  // Generally this should be disabled unless instructed otherwise

  "debug": false,


  // Default language of the server along with all new players

  ***"defaultLang":*** "en_us",


  // Generally its best to not change this, unless you are customizing how Server Essentials displays commands

  ***"langUrlBase":*** "https://raw.githubusercontent.com/Wurmcraft/Server-Essentials-2/1.12/Forge/language",

  //  Changes how the data is stored within Server Essentials, Valid Options are `File`, `Rest` where `Rest` requires additional setup, see below

  ***"dataStorageType":*** "File",

  // How many extra threads Server Essentials is allowed to create at a given time

  ***"supportThreads":*** 2,

  // How long before a given users data is unloaded from memory, after logging out (in seconds)

  ***"playerReloadTimeout":*** 60,

  // This allows Server Essentials to "override" other mods commands and add permission nodes to the given commands. All commands follow the `command.<name>` format for the permission nodes

  ***"overrideCommandPerms":*** true,

  // Log any commands run to console

  ***"logCommandsToConsole":*** true,

!!! note "Information"
      Settings below this only relate to the Rest database when `dataStorageType=Rest`

  // Username:Password for database / Rest Login (Used in the creation of the token used by the server for communication)

  ***"restAuth":*** "username:password",


  // URL of the rest database, Default page will display "Welcome to Server Essentials Rest API V0.X"


  ***"restURL":*** "https://rest.xxxx.com:5050/",


  // Amount of time in seconds between syncing data between the database

  ***"syncTime":*** 90

}
