package io.wurmatron.serveressentials.cli;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import io.wurmatron.serveressentials.utils.EncryptionUtils;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CommandParser {

  public static final Scanner scanner = new Scanner(System.in);

  public static void handleCommand() {
    System.out.print("CLI > ");
    String line = scanner.nextLine();
    if (!line.isEmpty()) {
      handle(line);
    }
    handleCommand();
  }

  public static void handleCommands() {
    try {
      TimeUnit.SECONDS.sleep(5);
      handleCommand();
    } catch (Exception e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void handle(String line) {
    line = line.toLowerCase();
    if (line.startsWith("stop")) {
      stop(line.split(" "));
    } else if (line.startsWith("help")) {
      help();
    } else if (line.startsWith("add server") || line.startsWith("addserver")) {
      addServer();
    } else {
      ServerEssentialsRest.LOG.info("Unknown command! Try help for a list of commands");
    }
  }

  private static void addServer() {
    System.out.println();
    System.out.println("- Add a new server");
    String serverName = ConfigLoader.askQuestion("Name / ID of the server", "server");
    String serverIP = ConfigLoader.askQuestion("IP of the server", "127.0.0.1");
    String token = EncryptionUtils.generateRandomString(32);
    String key = EncryptionUtils.generateRandomString(24);
    EndpointSecurity.addServer(serverName, serverIP, token, key);
    System.out.println("- Server '" + serverName
        + "' can connect using the following (Please save in a safe place)");
    System.out.println("Token '" + token + "'");
    System.out.println("Key: '" + key + "'");
    System.out.println(
        "Make sure the server is connecting via the correct ip along with having the correct name, unless it wont connect");
    System.out.println();
  }

  private static void stop(String[] args) {
    if (args.length == 1) {
      ServerEssentialsRest.LOG.info("Shutting Down!");
      ServerEssentialsRest.javalin.stop();
      try {
        ServerEssentialsRest.LOG.info("Shutting down background tasks");
        ServerEssentialsRest.executors.awaitTermination(5, TimeUnit.SECONDS);
      } catch (Exception e) {
        ServerEssentialsRest.LOG.error(e.getMessage());
      }
      // TODO Sent shutdown message to servers
      System.exit(1);
    }
  }

  private static void help() {
    displayHelp("stop", "Safely shutdown the management software");
    displayHelp("help", "Gives list of commands and uses");
    displayHelp("add server",
        "Helps add another server to the api, generating authentication for said server");
  }

  private static void displayHelp(String command, String message) {
    System.out.printf("- %-10s | %-80s %n", command, message);
  }
}
