package io.wurmatron.serveressentials.cli;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import java.util.Scanner;

public class CommandParser {

  public static final Scanner scanner = new Scanner(System.in);

  public static void handleCommands() {
    System.out.print("CLI > ");
    String line = scanner.nextLine();
    if (!line.isEmpty()) {
      handle(line);
    }
    handleCommands();
  }

  public static void handle(String line) {
    line = line.toLowerCase();
    if (line.startsWith("stop")) {
      stop(line.split(" "));
    }
  }

  private static void stop(String[] args) {
    if (args.length == 0) {
      ServerEssentialsRest.LOG.info("Shutting Down!");
      ServerEssentialsRest.javalin.stop();
      // TODO Sent shutdown message to servers
      System.exit(1);
    }
  }

}
