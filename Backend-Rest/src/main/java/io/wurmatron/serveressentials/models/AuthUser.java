package io.wurmatron.serveressentials.models;

public class AuthUser {

    public String type;
    public String[] perms;
    public String token;

    public AuthUser(String type, String[] perms, String token) {
        this.type = type;
        this.perms = perms;
        this.token = token;
    }
}
