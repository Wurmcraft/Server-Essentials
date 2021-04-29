package io.wurmatron.serveressentials.tests.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;

public class HTTPRequests {

    public static String BASE_URL;
    public static String authToken;

    /**
     * Send some data to a given URL as a json format
     *
     * @param type type of request (PUT, POST)
     * @param url  additional info in the url
     * @param data object to send to the url
     * @return https status code for https connection, if its a teapot then its not getting
     * a response back
     */
    private static int send(String type, String url, Object data) throws IOException {
        URL sendURL = new URL(BASE_URL + url);
        URLConnection connection = sendURL.openConnection();
        HttpURLConnection http = (HttpURLConnection) connection;
        http.setRequestMethod(type.toUpperCase());
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.setDoOutput(true);
        http.setRequestProperty("Cookie", "authentication=" + authToken + ";");
        String json = GSON.toJson(data).replaceAll("\n", "");
        connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
        connection.getOutputStream().write(json.getBytes());
        return http.getResponseCode();
    }

    /**
     * Send / get some data from the given URL
     *
     * @param type type of object to cast the url data to
     * @param path additional info about the url
     * @return The url as the given object
     */
    private static <T> T withReturn(String connectionMethod, String path, T data, Class<T> type) throws IOException {
        if (!path.isEmpty()) {
            URL sendURL = new URL(BASE_URL + path);
            URLConnection connection = sendURL.openConnection();
            HttpURLConnection http = (HttpURLConnection) connection;
            http.setRequestMethod(connectionMethod.toUpperCase());
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.setDoOutput(true);
            http.setRequestProperty("Cookie", "authentication=" + authToken + ";");
            if (data != null) {
                String json = GSON.toJson(data).replaceAll("\n", "");
                connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
                connection.getOutputStream().write(json.getBytes());
            }
            int responseCode = http.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == java.net.HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();
                return GSON.fromJson(response.toString(), type);
            }
        }
        return null;
    }

    /**
     * Sends a HTTP Post request with the provided data to the given URL
     *
     * @param url  URL Path, without the base
     * @param data data to send to the given url, in json format (automatically converted)
     * @return response code
     * @throws IOException An Error has occurred, invalid URL, Not Found, Invalid Json
     */
    public static int post(String url, Object data) throws IOException {
        return send("post", url, data);
    }

    /**
     * Sends a HTTP Post request with the provided data to the given URL
     *
     * @param url  URL Path, without the base
     * @param data data to send to the given url, in json format (automatically converted)
     * @return instance of the returned data
     * @throws IOException An Error has occurred, invalid URL, Not Found, Invalid Json
     */
    public static <T> T postWithReturn(String url, T data, Class<T> clazz) throws IOException {
        return withReturn("post", url, data, clazz);
    }

    /**
     * Sends a HTTP Put request with the provided data to the given URL
     *
     * @param url  URL Path, without the base
     * @param data data to send to the given url, in json format (automatically converted)
     * @return response code
     * @throws IOException An Error has occurred, invalid URL, Not Found, Invalid Json
     */
    public static int put(String url, Object data) throws IOException {
        return send("put", url, data);
    }

    /**
     * Sends a HTTP Patch request with the provided dat ato the given URL
     *
     * @param url  URL Path, without the base
     * @param data data to send to the given url, in json format (automatically converted)
     * @return response code
     * @throws IOException An Error has occurred, invalid URL, Not Found, Invalid Json
     */
    public static int patch(String url, Object data) throws IOException {
        return send("patch", url, data);
    }

    /**
     * Sends a GET request to the given URL and returns the data in the provided instances form
     *
     * @param url URL Path, without the base
     * @return object from the specified url in the provided format, error if not possible
     * @throws IOException An Error has occurred, invalid URL, Not Found, Invalid Json
     */
    public static <T> T get(String url, Class<T> type) throws IOException {
        return withReturn("GET", url, null, type);
    }

    /**
     * Sends a HTTP Delete request with the provided data to the given URL
     *
     * @param url  URL Path, without the base
     * @param data data to send to the given url, in json format (automatically converted)
     * @return instance of the returned data
     * @throws IOException An Error has occurred, invalid URL, Not Found, Invalid Json
     */
    public static <T> T deleteWithReturn(String url, T data, Class<T> clazz) throws IOException {
        return withReturn("DELETE", url,data,clazz);
    }
}
