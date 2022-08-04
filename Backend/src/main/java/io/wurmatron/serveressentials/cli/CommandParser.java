package io.wurmatron.serveressentials.cli;

import io.wurmatron.serveressentials.ServerEssentialsRest;
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
    } else if(line.startsWith("help")) {
     help();
    } else {
      ServerEssentialsRest.LOG.info("Unknown command! Try help for a list of commands");
    }
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
  }

  private static void displayHelp(String command, String message) {
    System.out.printf("- %-10s | %30s %n", command, message);
  }
}
