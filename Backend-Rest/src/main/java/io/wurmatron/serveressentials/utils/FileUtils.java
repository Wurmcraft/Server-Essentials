package io.wurmatron.serveressentials.utils;

import me.grison.jtoml.impl.Toml;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;

public class FileUtils {

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

    public static <T> T fromString(String data, Class<T> dataType) {
        return GSON.fromJson(data, dataType);
    }
}
