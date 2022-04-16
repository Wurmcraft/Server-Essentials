/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.io;

import io.wurmatron.server_essentials.backend.db.TableConfigurator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileUtils {

  /**
   * Reads a file from the jar and provides it as a string
   *
   * @param path exact path in the jar to read the data from
   * @return string of "file" from the internal jar
   * @throws IOException unable to find / read the provided file
   */
  public static String readInternalFile(String path) throws IOException {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          Objects.requireNonNull(TableConfigurator.class.getResourceAsStream(path))));
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (Exception e) {
      throw new IOException("Failed to read internal path '" + path + "'");
    }
  }

}
