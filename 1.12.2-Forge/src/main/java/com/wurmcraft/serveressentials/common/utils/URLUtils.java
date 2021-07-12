package com.wurmcraft.serveressentials.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLUtils {

    public static String get(String url) throws Exception {
        URLConnection con = new URL(url).openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null)
            response.append(inputLine);
        reader.close();
        return response.toString();
    }
}
