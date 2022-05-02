/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.server_essentials.backend.config;

import io.wurmatron.server_essentials.backend.ServerEssentialsBackend;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class TextFileConfigReaderAndLoader {

  public static <T extends Config> T load(T configInstance, File file) {
    try {
      List<String> fileData = Files.readAllLines(file.toPath());
      HashMap<String, String> mappedData = new HashMap<>();
      for (String field : fileData) {
        String f = field.substring(0, field.indexOf(":"));
        String data = field.substring(field.indexOf(":") + 1).trim();
        mappedData.put(f, data);
      }
      return mapToFields(configInstance, mappedData);
    } catch (IOException e) {
      ServerEssentialsBackend.LOG.warn(
          "Failed to read config file '"
              + configInstance.getName()
              + "."
              + configInstance.getConfigStyle().toString().toLowerCase());
      ServerEssentialsBackend.LOG.warn(e.getMessage());
    }
    return configInstance;
  }

  public static <T extends Config> String convertToString(T configInstance) {
    StringBuilder builder = new StringBuilder();
    HashMap<String, String> mappedFields = mapFields(configInstance);
    for (String field : mappedFields.keySet()) {
      builder.append(field).append(": ").append(mappedFields.get(field)).append("\n");
    }
    return builder.toString();
  }

  private static <T extends Config> HashMap<String, String> mapFields(T configInstance) {
    HashMap<String, String> classMap = new HashMap<>();
    for (Field field : configInstance.getClass().getDeclaredFields()) {
      try {
        // TODO Based on type not just via .toString()
        classMap.put(field.getName(), field.get(configInstance).toString());
      } catch (Exception e) {
        ServerEssentialsBackend.LOG.debug(
            "Failed to convert field '"
                + field.getName()
                + "' to string for config (txt) conversion");
        ServerEssentialsBackend.LOG.debug(e.getMessage());
      }
    }
    return classMap;
  }

  private static <T extends Config> T mapToFields(
      T configInstance, HashMap<String, String> mappedValues) {
    for (String field : mappedValues.keySet()) {
      try {
        Field f = configInstance.getClass().getDeclaredField(field);
        f.set(configInstance, mappedValues.get(field));
      } catch (Exception e) {
        ServerEssentialsBackend.LOG.debug(
            "Unable to find field '" + field + "' within '" + configInstance.getName() + "'");
        ServerEssentialsBackend.LOG.debug(e.getMessage());
      }
    }
    return configInstance;
  }
}
