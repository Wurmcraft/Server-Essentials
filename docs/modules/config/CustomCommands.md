# Custom Commands

***Location:*** `Server-Essentials/Misc/CustomCommands/"CommandName".json`

***Message Format:***

    "name": "Command Name",

    "aliases": ["Alternate Names"],

    "functions": [

      {

        "type": "MESSAGE",

        "values": [

          "&cMessage 1 to display",

          "&bMessage 2 to display",

        ]

      }

    ]

***Command Redirect Format:***

        "name": "Command Name",

        "aliases": ["Alternate Names"],

        "functions": [

          {

            "type": "COMMAND",

            "values": [

              "bal",

            ]

          }

        ]
