/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class HttpUtils {

  /**
   * Used to fix a known bug in the JDK
   * https://stackoverflow.com/questions/25163131/httpurlconnection-invalid-http-method-patch
   * Created by okutane
   *
   * @param methods methods to add support for
   */
  public static void allowMethods(String... methods) {
    try {
      Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
      methodsField.setAccessible(true);
      String[] oldMethods = (String[]) methodsField.get(null);
      Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
      methodsSet.addAll(Arrays.asList(methods));
      String[] newMethods = methodsSet.toArray(new String[0]);
      methodsField.set(null, newMethods);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
