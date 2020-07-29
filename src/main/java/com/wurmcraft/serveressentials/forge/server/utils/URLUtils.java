package com.wurmcraft.serveressentials.forge.server.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class URLUtils {

  /**
   * Reads a url to a string
   *
   * @param requestURL url to try to read
   *     <p>Copied from
   *     https://stackoverflow.com/questions/4328711/read-url-to-string-in-few-lines-of-java-code
   *     Credit goes to ccleve on StackOverflow
   */
  public static String readStringFromURL(String requestURL) throws IOException {
    try (Scanner scanner =
        new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

}
