/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class EncryptionUtils {

  public static String pepper = "";

  public static final String ALGO = "SHA-512";

  /**
   * Hashes the provided text with the provided salt, and pepper
   *
   * @param salt salt for the provided text
   * @param text text to be hashed
   * @return hashed version of the input text
   */
  public static String hash(String salt, String text) {
    try {
      MessageDigest md = MessageDigest.getInstance(ALGO);
      md.update((salt + pepper).getBytes());
      byte[] hashed = md.digest(text.getBytes());
      return new String(hashed);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Checks if the provided hash and the user password are the same
   *
   * @param hash hash of the previous password
   * @param salt salt of the previous password
   * @param text text to be hashed
   * @return if the 2 passwords match
   */
  public static boolean isSame(String hash, String salt, String text) {
    String hashedInput = hash(salt, text);
    return hash.equals(hashedInput);
  }

  public static final String[] CHARS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
      "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "2",
      "3", "4", "5", "6", "7", "8", "9"};

  private static Random RAND = new Random();

  public static String generateRandomString(int length) {
    StringBuilder builder = new StringBuilder();
    for (int x = 0; x < length; x++) {
      builder.append(CHARS[RAND.nextInt(CHARS.length)]);
    }
    return builder.toString();
  }
}
