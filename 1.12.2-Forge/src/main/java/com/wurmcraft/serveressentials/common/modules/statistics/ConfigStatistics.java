package com.wurmcraft.serveressentials.common.modules.statistics;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Statistics")
public class ConfigStatistics {

    public Player player;
    public Server server;
    public World world;

    public ConfigStatistics(Player player, Server server, World world) {
        this.player = player;
        this.server = server;
        this.world = world;
    }

    public ConfigStatistics() {
        this.player = new Player();
        this.server = new Server();
        this.world = new World();
    }

    public static class Player {

        public boolean deaths;

        public Player(boolean deaths) {
            this.deaths = deaths;
        }

        public Player() {
            this.deaths = true;
        }
    }

    public static class Commands {

        public boolean countCommands;

        public Commands(boolean countCommands) {
            this.countCommands = countCommands;
        }

        public Commands() {
            this.countCommands = true;
        }
    }

    public static class World {

        public boolean blockBroken;
        public boolean blocksPlaced;
        public boolean entityKills;
        public boolean groundItems;

        public World(boolean blockBroken, boolean blocksPlaced, boolean entityKills, boolean groundItems) {
            this.blockBroken = blockBroken;
            this.blocksPlaced = blocksPlaced;
            this.entityKills = entityKills;
            this.groundItems = groundItems;
        }

        public World() {
            this.blockBroken = true;
            this.blocksPlaced = true;
            this.entityKills = true;
            this.groundItems = true;
        }
    }

    public static class Server {

        public boolean ms;
        public boolean loadedChunks;
        public boolean playerCount;

        public Server(boolean ms, boolean loadedChunks, boolean playerCount) {
            this.ms = ms;
            this.loadedChunks = loadedChunks;
            this.playerCount = playerCount;
        }

        public Server() {
            this.ms = true;
            this.loadedChunks = true;
            this.playerCount = true;
        }
    }


}
