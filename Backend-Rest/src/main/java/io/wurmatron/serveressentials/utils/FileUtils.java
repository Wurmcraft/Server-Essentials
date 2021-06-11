package io.wurmatron.serveressentials.utils;

import me.grison.jtoml.impl.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;

/**
 * Utils designed to help with files, reading, writing, conversions
 */
public class FileUtils {

    /**
     * Converts a data instance into a string for storage
     *
     * @param data instance of data to be converted
     * @param type fileType to be converted into (JSON, TOML)
     * @param <T>  Instance of the class to be converted
     * @return string version in the specified format (type)
     * @see FileUtils#fromJsonString(String, Class)
     */
    public static <T> String toString(T data, String type) {
        switch (type.toUpperCase()) {
            case ("TOML"): {
                return Toml.serialize("config", data);
            }
            case ("JSON"): {
                return GSON.toJson(data);
            }
            default:
                return "";
        }
    }

    /**
     * Converts a given string back into its instance
     *
     * @param data     json data to be converted back into a instance
     * @param dataType Class Type for the given 'data' string
     * @param <T>      Type of the data to convert the string into
     * @return instance of the data from the provided string
     * @throws com.google.gson.JsonParseException If invalid json is detected
     * @see FileUtils#toString(Object, String)
     */
    public static <T> T fromJsonString(String data, Class<T> dataType) {
        return GSON.fromJson(data, dataType);
    }

    /**
     * Creates a new file with the provided data
     *
     * @param file File to write the data into
     * @param data data to be saved directly to the file
     * @return if the file was created without errors
     */
    public static boolean write(File file, String data) {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            Files.write(file.toPath(), data.getBytes(),StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
