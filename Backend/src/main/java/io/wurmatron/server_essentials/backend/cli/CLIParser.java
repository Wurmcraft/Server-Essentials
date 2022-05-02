/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.cli;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import io.wurmatron.server_essentials.backend.db.DatabaseConnector;
import java.util.Scanner;

public class CLIParser {

  public static final Scanner scanner = new Scanner(System.in);

  public static String getUserInput(String question) {
    System.out.print(question + " > ");
    return scanner.nextLine();
  }

  public static void handleCommand(String command) {
    if (command.equalsIgnoreCase("help")) {
      System.out.println("- help <display command information / usage>");
      System.out.println("- stop <gracefully shutdown the application>");
    } else if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("quit")) {
      ServerEssentialsBackend.LOG.warn("Shutting Down! ...");
      DatabaseConnector.getSession().close();
      ServerEssentialsBackend.javalin.stop();
      ServerEssentialsBackend.scheduledService.shutdown();
      // TODO Shutdown gracefully
      System.exit(1);
    } else {
      System.out.println("Unknown Command!, Try help for a list of commands");
    }
  }
}
