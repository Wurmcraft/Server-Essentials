package com.wurmcraft.server_essentials.rest.sql;

import com.wurmcraft.server_essentials.rest.api.NetworkUser;
import joptsimple.internal.Strings;

import java.util.regex.Pattern;

public class ParamChecker {

    public static final Pattern UUID_REGEX = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    public static final String ALPHA_NUMERIC_REGEX = "[A-Za-z0-9_]+";

    public static String sanitizeUUID(String uuid) {
        if (UUID_REGEX.matcher(uuid).find()) {
            return uuid;
        }
        return "";  // Invalid
    }

    public static String sanitizeName(String name) {
        if (name.matches(ALPHA_NUMERIC_REGEX))
            return name;
        else
            return "";
    }

    public static boolean isValid(NetworkUser user) {
        return true;
    }

    public static boolean returnBool(String str) {
        if(str == null || str.isEmpty())
            return false;
       if(str.equalsIgnoreCase("true") || str.equalsIgnoreCase("T")) {
           return true;
       }
       return false;
    }

    public static String getNetworkUserInfo(NetworkUser user, String name) {
        if(name.equalsIgnoreCase("username")) {
            return sanitizeName(user.username);
        } else if(name.equalsIgnoreCase("rank")) {
            return Strings.join(user.rank, " ");
        }
        return "";
    }
}
