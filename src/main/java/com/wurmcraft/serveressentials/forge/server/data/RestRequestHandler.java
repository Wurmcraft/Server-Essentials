package com.wurmcraft.serveressentials.forge.server.data;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.json.basic.CurrencyConversion;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.rest.CommandQueue;
import com.wurmcraft.serveressentials.forge.api.json.rest.DiscordToken;
import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;
import com.wurmcraft.serveressentials.forge.api.json.rest.RestValidate;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerChunkData;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

public class RestRequestHandler {

  public static final String USER_AGENT = "Mozilla/5.0";

  private String baseURL = parseConfigURL(SECore.config.Rest.restURL) + "api/";

  private static RestRequestHandler INSTANCE = new RestRequestHandler();
  public static RestValidate validate = null;

  public static String parseConfigURL(String url) {
    if (url.endsWith("/")) {
      return url;
    } else {
      return url + "/";
    }
  }

  /**
   * Send some data to a given URL as a json format
   *
   * @param type type of request (PUT, POST)
   * @param url additional info in the url
   * @param data object to send to the url
   * @return https status code for https connection, if its a teapot then its not getting
   * a response back
   */
  private int send(String type, String url, Object data) {
    try {
      URL sendURL = new URL(baseURL + url);
      URLConnection connection = sendURL.openConnection();
      HttpsURLConnection https = (HttpsURLConnection) connection;
      https.setRequestMethod(type.toUpperCase());
      https.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      https.setDoOutput(true);
      https.setRequestProperty("token", SECore.config.Rest.restAuth);
      String json = GSON.toJson(data).replaceAll("\n", "");
      connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
      connection.getOutputStream().write(json.getBytes());
      int status = ((HttpsURLConnection) connection).getResponseCode();
      if (status == HttpsURLConnection.HTTP_UNAUTHORIZED) {
        ServerEssentialsServer.LOGGER.fatal("Invalid Rest API Key, Unable to Put");
      }
      return status;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 418; //  I'm a teapot
  }

  public int post(String url, Object data) {
    return send("post", url, data);
  }

  public int put(String url, Object data) {
    return send("put", url, data);
  }

  /**
   * Get the given data from the url and
   *
   * @param type type of object to cast the url data to
   * @param data additional info about the url
   * @return The url as the given object
   */
  public <T extends Object> T get(String data, Class<T> type) {
    if (!data.isEmpty()) {
      try {
        URL obj = new URL(baseURL + data);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("token", SECore.config.Rest.restAuth);
        con.setReadTimeout(300000);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK
            || responseCode == HttpsURLConnection.HTTP_ACCEPTED) {
          BufferedReader in = new BufferedReader(
              new InputStreamReader(con.getInputStream()));
          String inputLine;
          StringBuilder response = new StringBuilder();
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();
          return GSON.fromJson(response.toString(), type);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static class Rank {

    public static com.wurmcraft.serveressentials.forge.api.json.basic.Rank getRank(
        String name) {
      return INSTANCE.get(
          "ranks/" + name, com.wurmcraft.serveressentials.forge.api.json.basic.Rank.class);
    }

    public static int addRank(
        com.wurmcraft.serveressentials.forge.api.json.basic.Rank rank) {
      return INSTANCE.post("ranks/add", rank);
    }

    public static int overrideRank(
        com.wurmcraft.serveressentials.forge.api.json.basic.Rank rank) {
      return INSTANCE.post("ranks/" + rank.getID() + "/override", rank);
    }

    public static int deleteRank(
        com.wurmcraft.serveressentials.forge.api.json.basic.Rank rank) {
      return INSTANCE.post("ranks/" + rank.getID() + "/del", rank);
    }

    public static com.wurmcraft.serveressentials.forge.api.json.basic.Rank[] getAllRanks() {
      return INSTANCE
          .get("ranks/", com.wurmcraft.serveressentials.forge.api.json.basic.Rank[].class);
    }
  }

  public static class User {

    public static GlobalPlayer getPlayer(String uuid) {
      return INSTANCE.get("users/" + uuid, GlobalPlayer.class);
    }

    public static int addPlayer(GlobalPlayer player) {
      return INSTANCE.post("users/add", player);
    }

    public static int overridePlayer(String uuid, GlobalPlayer player) {
      return INSTANCE.put("users/" + uuid + "/override", player);
    }
  }

  public static class Verify {

    public static RestValidate get() {
      return INSTANCE.get("validate", RestValidate.class);
    }
  }

  public static class Track {

    public static int updateTrack(ServerStatus status) {
      return INSTANCE.post("status", status);
    }

    public static ServerStatus[] getStatus() {
      return INSTANCE.get("status", ServerStatus[].class);
    }
  }

  public static class Discord {

    public static DiscordToken[] getTokens() {
      return INSTANCE.get("discord/list", DiscordToken[].class);
    }
  }

  public static class Economy {

    public static CurrencyConversion[] getAllCurrencies() {
      return INSTANCE.get("eco", CurrencyConversion[].class);
    }

    public static CurrencyConversion getCurrency(String name) {
      return INSTANCE.get("eco/" + name, CurrencyConversion.class);
    }

    public static int addCurrency(CurrencyConversion currency) {
      return INSTANCE.post("eco/add", currency);
    }

    public static int overrideCurrency(CurrencyConversion currency) {
      return INSTANCE.put("/currency" + currency.name + "/override", currency);
    }

    public static int delCurrency(CurrencyConversion currency) {
      return INSTANCE.put("/currency" + currency.name + "/del", currency);
    }
  }

  public static class ChunkLoading {

    public static ServerChunkData getLoadedChunks() {
      return INSTANCE.get(
          "chunkloading/" + SECore.config.serverID, ServerChunkData.class);
    }

    public static int overrideLoadedChunks(ServerChunkData data) {
      return INSTANCE.post("chunkloading/add", data);
    }
  }

  public static class AutoRank {

    public static com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank[] getAutoRanks() {
      return INSTANCE.get(
          "autoRanks",
          com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank[].class);
    }

    public static com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank getAutoRank(
        String rank) {
      return INSTANCE.get(
          "autoRanks/" + rank,
          com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank.class);
    }

    public static int addAutoRank(
        com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank rank) {
      return INSTANCE.post("autoRanks/add", rank);
    }

    public static int deleteAutoRank(
        com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank ar) {
      return INSTANCE.put("autoRanks/" + ar.getID() + "/del", ar);
    }

    public static int overrideAutoRank(
        com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank rank) {
      return INSTANCE.post("autoRanks/" + rank.getID() + "/override", rank);
    }
  }

  public static class Ban {

    public static GlobalBan[] getGlobalBans() {
      return INSTANCE.get("ban/", GlobalBan[].class);
    }

    public static int addGlobalBans(GlobalBan ban) {
      return INSTANCE.post("bans/add", ban);
    }

    public static int deleteBan(GlobalBan ban) {
      return INSTANCE.put("bans/" + ban.getID() + "/del", ban);
    }
  }

  public static class Commands {
    public static CommandQueue[] getCommandQueue() {
      return INSTANCE.get("commands", CommandQueue[].class);
    }

    public static int addCommandQueue(CommandQueue queue) {
      return INSTANCE.post("commands/add", queue);
    }
  }
}