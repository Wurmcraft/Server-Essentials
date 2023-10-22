package com.wurmcraft.serveressentials.common.data.loader;

import org.cliffc.high_scale_lib.NonBlockingHashMap;

public interface IDataLoader {

  /**
   * Get a map of [key, data] for the requested dataType. This will only return the current data
   * within the cache, which may or not be all the entries for the requested type
   *
   * @param key type of data you are looking for
   * @param type cast the data to this type
   */
  <T extends Object> NonBlockingHashMap<String, T> getFromKey(DataLoader.DataType key, T type);

  /**
   * Get a specific entry based on its type and key
   *
   * @param type type / category of this entry
   * @param key key / ID of the entry
   */
  Object get(DataLoader.DataType type, String key);

  /**
   * Get a specific entry based on its type and key, casts the result based on the provided type
   * Same as #get(DataLoader.DataType, String), except it casts based on the provided type
   *
   * @param type type / category of this entry
   * @param key key / ID of the entry
   * @param data instance of the data to cast the object into
   * @see #get(DataLoader.DataType, String)
   */
  <T extends Object> T get(DataLoader.DataType type, String key, T data);

  /**
   * Creates a new entry with the provided key and instance, it will not update / override existing
   * entries (same key)
   *
   * @param type type / category of this entry
   * @param key key / ID of the entry
   * @param data instance you want to be created / "registered"
   * @see #update(DataLoader.DataType, String, Object)
   */
  boolean register(DataLoader.DataType type, String key, Object data);

  /**
   * Updates an existing entry with the updated / newer version, it will not create a new entry if
   * one does not exist (same key)
   *
   * @param type type / category of this entry
   * @param key key / ID of the entry
   * @param data instance you want to be updated
   */
  boolean update(DataLoader.DataType type, String key, Object data);

  /**
   * Deletes an existing entry
   *
   * @param type type / category of the entry
   * @param key key / ID of the entry
   * @param cacheOnly remove from cache-only for 'false' for a full delete
   */
  boolean delete(DataLoader.DataType type, String key, boolean cacheOnly);

  /**
   * Deletes an existing entry Will not fully delete the given entry, will only remove from cache,
   * same as 'cacheOnly=true'
   *
   * @param type type / category of the entry
   * @param key key / ID of the entry
   */
  boolean delete(DataLoader.DataType type, String key);
}
