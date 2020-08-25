package com.wurmcraft.bot;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.bot.json.api.data.Token;
import com.wurmcraft.bot.json.api.eco.Currency;
import com.wurmcraft.bot.json.api.json.CommandQueue;
import com.wurmcraft.bot.json.api.json.CommandQueue.RequestedCommand;
import com.wurmcraft.bot.json.api.json.Validation;
import com.wurmcraft.bot.json.api.player.GlobalPlayer;
import com.wurmcraft.bot.json.api.status.TrackingStatus;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

public class RequestGenerator {

  private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static final String USER_AGENT = "Mozilla/5.0";

  private String auth = createRestAuth(DiscordBot.auth);
  private String baseURL = parseConfigURL(DiscordBot.baseURL) + "api/";

  public static RequestGenerator INSTANCE = new RequestGenerator();

  private static boolean isBase64(String data) {
    try {
      Base64.getDecoder().decode(data.getBytes());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static String createRestAuth(String auth) {
    if (isBase64(auth)) {
      return "Basic " + auth;
    } else {
      return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
  }

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
      https.setRequestProperty("Authorization", auth);
      String json = GSON.toJson(data).replaceAll("\n", "");
      connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
      connection.getOutputStream().write(json.getBytes());
      int status = ((HttpsURLConnection) connection).getResponseCode();
      if (status == HttpsURLConnection.HTTP_UNAUTHORIZED) {
        System.out.println("Invalid Rest API Key, Unable to Put");
      }
      return status;
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
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
        con.setRequestProperty("Authorization", auth);
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
        System.out.println(e.getMessage());
      }
    }
    return null;
  }

  public static class Rank {

    public static com.wurmcraft.bot.json.api.json.rank.Rank getRank(String name) {
      return INSTANCE.get(
          "rank/" + name, com.wurmcraft.bot.json.api.json.rank.Rank.class);
    }

    public static int addRank(com.wurmcraft.bot.json.api.json.rank.Rank rank) {
      return INSTANCE.post("rank/add", rank);
    }

    public static int overrideRank(com.wurmcraft.bot.json.api.json.rank.Rank rank) {
      return INSTANCE.post("rank/" + rank.getID() + "/override", rank);
    }

    public static int deleteRank(com.wurmcraft.bot.json.api.json.rank.Rank rank) {
      return INSTANCE.post("rank/del", rank);
    }

    public static com.wurmcraft.bot.json.api.json.rank.Rank[] getAllRanks() {
      return INSTANCE.get("rank/", com.wurmcraft.bot.json.api.json.rank.Rank[].class);
    }
  }

  public static class User {

    public static GlobalPlayer getPlayer(String uuid) {
      return INSTANCE.get("user/" + uuid, GlobalPlayer.class);
    }

    public static int addPlayer(GlobalPlayer player) {
      return INSTANCE.post("user/add", player);
    }

    public static int overridePlayer(String uuid, GlobalPlayer player) {
      return INSTANCE.put("user/" + uuid + "/override", player);
    }
  }

  public static class Verify {

    public static Validation get() {
      return INSTANCE.get("validate", Validation.class);
    }
  }

  public static class Track {

    public static int updateTrack(TrackingStatus status) {
      return INSTANCE.post("status", status);
    }

    public static TrackingStatus[] getStatus() {
      return INSTANCE.get("status", TrackingStatus[].class);
    }
  }

  public static class Discord {

    public static Token[] getTokens() {
      return INSTANCE.get("discord/list", Token[].class);
    }
  }

  public static class Economy {

    public static Currency[] getAllCurrencies() {
      return INSTANCE.get("eco", Currency[].class);
    }

    public static Currency getCurrency(String name) {
      return INSTANCE.get("eco/" + name, Currency.class);
    }

    public static int addCurrency(Currency currency) {
      return INSTANCE.post("eco/add", currency);
    }

    public static int overrideCurrency(Currency currency) {
      return INSTANCE.put("/eco" + currency.name + "/override", currency);
    }
  }

  public static class AutoRank {

    public static com.wurmcraft.bot.json.api.json.rank.AutoRank[] getAutoRanks() {
      return INSTANCE.get(
          "autorank", com.wurmcraft.bot.json.api.json.rank.AutoRank[].class);
    }

    public static com.wurmcraft.bot.json.api.json.rank.AutoRank getAutoRank(
        String rank) {
      return INSTANCE.get(
          "autorank/" + rank, com.wurmcraft.bot.json.api.json.rank.AutoRank.class);
    }

    public static int addAutoRank(com.wurmcraft.bot.json.api.json.rank.AutoRank rank) {
      return INSTANCE.post("autorank/add", rank);
    }
  }

  public static class Commands {

    public static CommandQueue[] getCommandQueue() {
      return INSTANCE.get("commands", CommandQueue[].class);
    }

    public static int addCommandQueue(CommandQueue queue) {
      return INSTANCE.post("commands/add", queue);
    }

    public static void newCommand(RequestedCommand command) {
      CommandQueue[] queue = getCommandQueue();
      for (CommandQueue q : queue) {
        if (q.commands.length > 0 && q.commands[0].serverID.equals(command.serverID)) {
          addCommandQueue(new CommandQueue(new RequestedCommand[] {command}));
//          List<RequestedCommand> commands = new ArrayList<>();
//          commands.add(command);
//          System.out.println(addCommandQueue(new CommandQueue(commands.toArray(new RequestedCommand[0]))));
        }
      }
    }
  }
}
