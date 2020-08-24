package com.wurmcraft.serveressentials.forge.api.json.rest;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import java.time.Instant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * Used to store / load chunkloading from the database
 */
public class ServerChunkData implements JsonParser {

  public String serverID;
  public PlayerChunkData[] playerChunks;

  public ServerChunkData(String serverID) {
    this.serverID = serverID;
    this.playerChunks = new PlayerChunkData[0];
  }

  public ServerChunkData(String serverID,
      PlayerChunkData[] playerChunks) {
    this.serverID = serverID;
    this.playerChunks = playerChunks;
  }

  public static class PlayerChunkData implements JsonParser {

    public String uuid;
    public ChunkPos chunkPos;
    public double storedCurrency;
    public long setupTime;

    public PlayerChunkData(String uuid, ChunkPos chunkPos) {
      this.uuid = uuid;
      this.chunkPos = chunkPos;
      this.storedCurrency = 0;
      this.setupTime = Instant.EPOCH.toEpochMilli();
    }

    public PlayerChunkData(String uuid, ChunkPos chunkPos, double storedCurrency,
        long setupTime) {
      this.uuid = uuid;
      this.chunkPos = chunkPos;
      this.storedCurrency = storedCurrency;
      this.setupTime = setupTime;
    }

    public PlayerChunkData() {
      this.uuid = "";
      BlockPos blockPos;
      this.chunkPos = new ChunkPos(0,0);
      this.storedCurrency = 0;
      this.setupTime = 0;
    }

    @Override
    public String getID() {
      return uuid;
    }
  }

  @Override
  public String getID() {
    return serverID;
  }
}
