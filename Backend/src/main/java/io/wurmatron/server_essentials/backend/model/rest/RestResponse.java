package io.wurmatron.server_essentials.backend.model.rest;

public class RestResponse {

  public String title;
  public String message;
  public String providedData;

  public RestResponse(String title, String message, String providedData) {
    this.title = title;
    this.message = message;
    this.providedData = providedData;
  }
}
