package com.wurmcraft.server_essentials.rest.sql;

import com.wurmcraft.server_essentials.rest.SE_Rest;
import com.wurmcraft.server_essentials.rest.api.NetworkUser;
import joptsimple.internal.Strings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLCommands {

    public static NetworkUser getUserByUUID(String uuid) throws SQLException {
        return getUser("uuid", uuid);
    }

    public static NetworkUser getUserByName(String username) throws SQLException {
        return getUser("username", username);
    }

    public static NetworkUser getUser(String type, String data) throws SQLException {
        Statement statement = SE_Rest.connector.getConnection().createStatement();
        String query = "SELECT * FROM users WHERE uuid='%TYPE%' LIMIT 1;".replaceAll("%TYPE%", data).replaceAll("uuid", type);
        ResultSet result = statement.executeQuery(query);
        if (result.next()) {
            NetworkUser user = new NetworkUser();
            user.uuid = result.getString("uuid");
            user.username = result.getString("username");
            user.rank = result.getString("rank").split(" ");
            return user;
        }
        return null;
    }

    public static void addUser(NetworkUser user) throws SQLException {
        Statement statement = SE_Rest.connector.getConnection().createStatement();
        String query = "INSERT INTO `users` (`uuid`, `username`, `rank`) VALUES ('%UUID%','%USERNAME', '%RANK%')".replace("%UUID%", user.uuid).replace("%USERNAME%", user.username).replace("%RANK%", Strings.join(user.rank, " "));
        statement.execute(query);
    }
}
