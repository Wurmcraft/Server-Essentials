package com.wurmcraft.serveressentials.common.utils;

import com.wurmcraft.serveressentials.ServerEssentials;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestGenerator {

  public static final String USER_AGENT = "Mozilla/5.0";

  // Connection Specific
  public static final String BASE_URL = parseConfigURL(
      ServerEssentials.config.storage.baseURL);
  public static String token = "";

  // Regex
  public static final Pattern IP_REGEX =
      Pattern.compile(
          "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

  // Setup
  static {
    allowMethods("PATCH"); // Fix JVM Bug
  }

  /**
   * Check and adds leading and trailing parts of a url if they are missing This does not
   * check if the url is valid or not, it simple makes sure it has the leading and
   * trailing of a url
   *
   * @param url string to be tested / converted into a url
   */
  private static String parseConfigURL(String url) {
    // Check for trailing /
    if (!url.endsWith("/")) {
      url = url + "/";
    }
    // Check for leading http or https
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      url =
          IP_REGEX.matcher(url.substring(0, url.length() - 1)).find()
              ? "http://" + url
              : "https://" + url;
    }
    return url;
  }

  /**
   * Adds any and all authentication needed by the connection
   *
   * @param connection url connection to add the auth onto
   */
  private static HttpURLConnection addAuthentication(HttpURLConnection connection) {
    connection.setRequestProperty("Cookie", "authentication=" + token + ";");
    return connection;
  }

  /**
   * Send the provided data with the given method to the provided URL
   *
   * @param type http method type, POST, PUT, DELETE, GET
   * @param url endpoint without the base url to send the request to
   * @param data instance to be send in the body, as json
   * @return collected data from the connection placed into a wrapper
   * @throws IOException error with the url connection
   */
  private static HttpResponse http(String type, String url, Object data)
      throws IOException {
    // Setup Http Connection
    URL sendURL = new URL(BASE_URL + url);
    URLConnection connection = sendURL.openConnection();
    HttpURLConnection http = (HttpURLConnection) connection;
    http.setRequestMethod(type.toUpperCase());
    http.setRequestProperty("User-Agent", USER_AGENT);
    http.setDoOutput(true);
    // Handle Auth
    http = addAuthentication(http);
    // Add / Write  body (if any)
    if (data != null) {
      String bodyJson = ServerEssentials.GSON.toJson(data);
      http.setRequestProperty("Content-Length", String.valueOf(bodyJson.length()));
      http.setRequestProperty("Content-Type", "application/json");
      http.getOutputStream().write(bodyJson.getBytes(StandardCharsets.UTF_8));
    }
    // Collect and return
    String httpBody = "";
    try {
      httpBody =
          new BufferedReader(new InputStreamReader(http.getInputStream()))
              .lines()
              .collect(Collectors.joining("\n"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new HttpResponse(http.getResponseCode(), http.getHeaderFields(), httpBody);
  }

  /**
   * Send a HTTP GET request to the requested url with the provided information
   *
   * @param path URL path without the base
   * @param queryParams map of the query params to be applied to get request
   * @return response instance (status, headers, response)
   * @throws IOException error with the url connection
   */
  public static HttpResponse get(String path, Map<String, String> queryParams)
      throws IOException {
    StringBuilder builder = new StringBuilder();
    if (queryParams != null && queryParams.size() > 0) {
      for (String key : queryParams.keySet()) {
        builder.append("?").append(key).append("=").append(queryParams.get(key));
      }
    }
    return http("GET", path + builder, null);
  }

  /**
   * Send a HTTP GET request to the requested url with the provided information
   *
   * @param path URL path without the base
   * @return response instance (status, headers, response)
   * @throws IOException error with the url connection
   */
  public static HttpResponse get(String path) throws IOException {
    return get(path, null);
  }

  /**
   * Send a HTTP POST request to the requested url with the provided information
   *
   * @param path URL path without the base
   * @param data instance of data to be send in the body (as json)
   * @return response instance (status, headers, response)
   * @throws IOException error with the url connection
   */
  public static HttpResponse post(String path, Object data) throws IOException {
    return http("POST", path, data);
  }

  /**
   * Send an HTTP PUT request to the requested url with the provided information
   *
   * @param path URL path without the base
   * @param data instance of data to be send in the body (as json)
   * @return response instance (status, headers, response)
   * @throws IOException error with the url connection
   */
  public static HttpResponse put(String path, Object data) throws IOException {
    return http("PUT", path, data);
  }

  /**
   * Send a HTTP DELETE request to the requested url with the provided information
   *
   * @param path URL path without the base
   * @param data instance of data to be send in the body (as json)
   * @return response instance (status, headers, response)
   * @throws IOException error with the url connection
   */
  public static HttpResponse delete(String path, Object data) throws IOException {
    return http("DELETE", path, data);
  }

  /**
   * Send a HTTP PATCH request to the requested url with the provided information
   *
   * @param path URL path without the base
   * @param data instance of data to be send in the body (as json)
   * @return response instance (status, headers, response)
   * @throws IOException error with the url connection
   */
  public static HttpResponse patch(String path, Object data) throws IOException {
    return http("PATCH", path, data);
  }

  public static class HttpResponse {

    public int status;
    public Map<String, List<String>> headers;
    public String response;

    /**
     * Wrapper from a http connection / response
     *
     * @param status http status code
     * @param headers headers that returned from the connection
     * @param response string response from the connection
     */
    public HttpResponse(int status, Map<String, List<String>> headers, String response) {
      this.status = status;
      this.headers = headers;
      this.response = response;
    }
  }

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
