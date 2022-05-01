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
