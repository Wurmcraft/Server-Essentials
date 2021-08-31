package com.wurmcraft.serveressentials.api.models;

public class LoginEntry {

    public String type;
    public String id;
    public String auth;
    public String validation;

    public LoginEntry(String type, String id, String auth, String validation) {
        this.type = type;
        this.id = id;
        this.auth = auth;
        this.validation = validation;
    }
}
