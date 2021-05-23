package io.wurmatron.serveressentials.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtils {

    // TODO Add to config / auto-generate
    public static String pepper = "test";

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
}
