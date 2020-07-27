package com.wurmcraft.serveressentials.forge.api.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import java.util.NoSuchElementException;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

/**
 * DataHandler is a basic class designed to provide an interface for the different data storage
 * types available within ServerEssentials
 *
 */
public interface IDataHandler {

  /**
   * Returns all the values that have been stored under this key
   *
   * @param key Key the value was stored under
   * @return map of all the values related to the DataKey
   */
  <T extends JsonParser> NonBlockingHashMap<String, T> getDataFromKey(DataKey key, T type);

  /**
   * Get specific data from the database when you know its DataKey and its ID
   *
   * @param key Key the value was stored under
   * @param dataID ID of the data that was stored
   * @return instance of the data that was stored
   */
  JsonParser getData(DataKey key, String dataID) throws NoSuchElementException;

  /**
   * Adds a given instance to a database based on its ID
   *
   * @param key Key the value was stored under
   * @param dataToStore instance of the data you wish to get stored
   */
  void registerData(DataKey key, JsonParser dataToStore);

  /**
   * Removes  instance from storage
   *
   * @param key Key the value was stored under
   * @param dataToRemove instance with the same ID was the one you wish to remove
   * @param  deleteFromDisk Delete data from disk once removed
   */
  void delData(DataKey key, String dataToRemove, boolean deleteFromDisk) throws NoSuchElementException;

}