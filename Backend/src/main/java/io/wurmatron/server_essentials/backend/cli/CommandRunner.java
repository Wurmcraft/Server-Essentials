/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.cli;

public class CommandRunner {

  public static void nextLine() {
    System.out.print("> ");
  }

  public static void handleCommands() {
    nextLine();
    do {
      String line = CLIParser.scanner.nextLine().trim();
      if (line.length() > 0) {
        CLIParser.handleCommand(line);
        nextLine();
      } else {
        System.out.println("Please input a command!");
        nextLine();
      }
    } while (true);
  }
}
