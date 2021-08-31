package com.wurmcraft.serveressentials.api.event;

import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DataRequestEvent extends Event {

    public DataLoader.DataType type;
    public String key;
    public Object data;

    /**
     * Used to collect unique data
     *
     * @param type type of data that is being requested
     * @param key  Id of the key being requested
     */
    public DataRequestEvent(DataLoader.DataType type, String key) {
        this.type = type;
        this.key = key;
        this.data = null;
    }
}
