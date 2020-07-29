package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.data.IDataHandler;
import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.util.NoSuchElementException;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class BasicDataHandler implements IDataHandler {

  protected NonBlockingHashMap<DataKey, NonBlockingHashMap<String, JsonParser>> loadedData = new NonBlockingHashMap<>();


  @Override
  public <T extends JsonParser> NonBlockingHashMap<String, T> getDataFromKey(DataKey key,
      T type) {
    NonBlockingHashMap<String, T> keyData = new NonBlockingHashMap<>();
    NonBlockingHashMap<String, JsonParser> storedData = loadedData
        .get(loadedData.get(key));
    if (loadedData.containsKey(key)) {
      for (String k : storedData.keySet()) {
        try {
          keyData.put(k, (T) storedData.get(k));
        } catch (Exception e) {
          if (SECore.config.debug) {
            ServerEssentialsServer.LOGGER
                .info(key.getName() + " does not contain " + type.toString() + " (" + e
                    .getMessage() + ")");
          }
        }
      }
    }
    return keyData;
  }

  @Override
  public JsonParser getData(DataKey key, String dataID) throws NoSuchElementException {
    if (loadedData.containsKey(key) && loadedData.get(key).containsKey(dataID)) {
      return loadedData.get(key).get(dataID);
    }
    throw new NoSuchElementException();
  }

  @Override
  public void registerData(DataKey key, JsonParser dataToStore) {
    if (loadedData.containsKey(key)) {
      NonBlockingHashMap<String, JsonParser> currentData = loadedData.get(key);
      currentData.put(dataToStore.getID(), dataToStore);
      loadedData.put(key, currentData);
    } else {
      NonBlockingHashMap<String, JsonParser> newDataEntry = new NonBlockingHashMap<>();
      newDataEntry.put(dataToStore.getID(), dataToStore);
      loadedData.put(key, newDataEntry);
    }
  }

  @Override
  public void delData(DataKey key, String dataToRemove, boolean deleteFromDisk)
      throws NoSuchElementException {
    if (loadedData.containsKey(key) && loadedData.get(key).containsKey(dataToRemove)) {
      loadedData.get(key).remove(dataToRemove);
    }
    throw new NoSuchElementException();
  }
}
