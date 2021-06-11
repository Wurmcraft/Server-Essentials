package io.wurmatron.serveressentials.models;

public class Event {

    public String event;
    public String type;
    public String jsonData;

    public Event(String event, String type, String jsonData) {
        this.event = event;
        this.type = type;
        this.jsonData = jsonData;
    }
}
