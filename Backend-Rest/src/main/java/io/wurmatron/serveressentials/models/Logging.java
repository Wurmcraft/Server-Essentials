package io.wurmatron.serveressentials.models;

public class Logging {

    public String serverID;
    public long timestamp;
    public String actionType;
    public String actionData;
    public String uuid;
    public int x;
    public int y;
    public int z;
    public int dim;

    /**
     * @param serverID   id of the server this occurred
     * @param timestamp  unix timestamp for when this action occurred
     * @param actionType name / type of action that has occurred
     * @param actionData data related to the action
     * @param uuid       uuid of the user that this is related to
     * @param x          x pos that this occurred (if applicable)
     * @param y          y pos that this occurred (if applicable)
     * @param z          z pos that this occurred (if applicable)
     * @param dim       dimension that this occurred (if applicable)
     */
    public Logging(String serverID, long timestamp, String actionType, String actionData, String uuid, int x, int y, int z, int dim) {
        this.serverID = serverID;
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.actionData = actionData;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }
}
