package com.wurmcraft.serveressentials.common.data.loader;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.google.gson.JsonParseException;
import com.wurmcraft.serveressentials.api.event.DataRequestEvent;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.FileUtils;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class FileDataLoader extends DataLoader {

  public static String SAVE_FOLDER = "Storage";

  static {
    MinecraftForge.EVENT_BUS.register(new SpecialDataCollector());
  }

  /**
   * Loads the requested folder if its not in cache
   *
   * @param key type of data you are looking for
   * @param type cast the data to this type
   */
  @Override
  public <T> NonBlockingHashMap<String, T> getFromKey(DataType key, T type) {
    NonBlockingHashMap<String, T> cachedData = super.getFromKey(key, type);
    File saveDir =
        new File(
            SAVE_DIR + File.separator + SAVE_FOLDER + File.separator + key.name()
                .toLowerCase());
    if (saveDir.exists()) {
      // Check if the cache data count is the same (don't check for same entries only the same
      // count)
      if (saveDir.list() != null
          && cachedData != null
          && saveDir.list().length == cachedData.size()) {
        return cachedData;
      }
      // Load each file
      for (File file : saveDir.listFiles()) {
        try {
          T fileInstance = (T) load(file, type.getClass());
          if (fileInstance != null) {
            cache(key, file.getName().replaceAll(".json", ""), fileInstance);
          } else {
            LOG.debug("Failed to load / cache '" + file.getName().replaceAll(".json", "")
                + "'");
          }
        } catch (IOException e) {
          LOG.error("Failed to read '" + file.getAbsolutePath() + "'!");
        }
      }
      return super.getFromKey(key, type);
    }
    return null;
  }

  /**
   * Loads a file from json into its representative type
   *
   * @param file file to attempt to read from
   * @param type type of data that's expected within the file
   * @throws IOException failed to read the file
   */
  private <T> T load(File file, Class<T> type) throws IOException {
    String json = FileUtils.readFileToString(file);
    try {
      return GSON.fromJson(json, type);
    } catch (JsonParseException e) {
      LOG.debug("Failed to parse '" + file.getAbsolutePath() + "'");
    }
    return null;
  }

  /**
   * Adds a instance to the cache
   *
   * @param type type of data to add to the cache
   * @param key the key / ID of the data to be cached
   * @param data instance to be added to the cache
   */
  protected void cache(DataType type, String key, Object data) {
    if (storage.containsKey(type)) {
      storage.get(type).put(key, new Object[]{System.currentTimeMillis(), data});
    } else {
      NonBlockingHashMap<String, Object[]> newCache = new NonBlockingHashMap<>();
      newCache.put(key,
          new Object[]{System.currentTimeMillis() + getTimeout(type), data});
      storage.put(type, newCache);
    }
  }

  /**
   * Create the file instance based on the storage category and its key
   *
   * @param type category of the data to be stored
   * @param key key / ID of the data to get stored
   */
  protected File getFile(DataType type, String key) {
    return new File(
        SAVE_DIR
            + File.separator
            + SAVE_FOLDER
            + File.separator
            + type.name().toLowerCase()
            + File.separator
            + key
            + ".json");
  }

  /**
   * Finds the requested data based on its key, if its cached its returned if not the file
   * is attempted to be loaded
   *
   * @param type type of data to look for
   * @param key id of the data you are looking for
   */
  @Nullable
  @Override
  public Object get(DataType type, String key) {
    Object cachedData = super.get(type, key);
    if (cachedData != null) {
      return cachedData;
    }
    File file = getFile(type, key);
    if (file.exists()) {
      try {
        Object instance = load(file, type.instanceType);
        if (instance != null) {
          cache(type, key, instance);
          return instance;
        }
      } catch (IOException e) {
        LOG.error("Failed to read '" + file.getAbsolutePath() + "'!");
      }
    } else {
      DataRequestEvent event = new DataRequestEvent(type, key);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.data != null) {
        register(type, key, event.data);
        return event.data;
      }
    }
    return null;
  }

  /**
   * Finds the requested data based on its key, if its cached its returned if not the file
   * is attempted to be loaded
   *
   * @param type type of data to look for
   * @param key id of the data you are looking for
   * @see #get(DataType, String)
   */
  @Override
  public <T> T get(DataType type, String key, T data) {
    return (T) get(type, key);
  }

  /**
   * Create a new entry, including the required storage files
   *
   * @param type type / category of this entry
   * @param key key / ID of the entry
   * @param data instance you want to be created / "registered"
   * @see #update(DataType, String, Object)
   */
  @Override
  public boolean register(DataType type, String key, Object data) {
    if (super.register(type, key, data)) {
      File file = getFile(type, key);
      file.getParentFile().mkdirs();
      if (!file.exists()) {
        try {
          if (file.createNewFile()) {
            FileUtils.writeByteArrayToFile(file, GSON.toJson(data).getBytes());
            return true;
          }
        } catch (IOException e) {
          e.printStackTrace();
          LOG.warn(
              "Failed to write '"
                  + file.getAbsolutePath()
                  + "' for type '"
                  + type.name()
                  + "' key '"
                  + key
                  + "'");
        }
      } else {
        LOG.debug(
            "Failed to register '"
                + file.getAbsolutePath()
                + "' for type '"
                + type.name()
                + "' key '"
                + key
                + "'");
      }
    }
    return false;
  }

  /**
   * Updates an existing entry, including the storage files
   *
   * @param type type / category of this entry
   * @param key key / ID of the entry
   * @param data instance you want to be updated
   * @see #register(DataType, String, Object)
   */
  @Override
  public boolean update(DataType type, String key, Object data) {
    if (super.update(type, key, data)) {
      File file = getFile(type, key);
      if (file.exists()) {
        try {
          FileUtils.writeByteArrayToFile(file, GSON.toJson(data).getBytes());
          return true;
        } catch (IOException e) {
          e.printStackTrace();
          LOG.warn(
              "Failed to write '"
                  + file.getAbsolutePath()
                  + "' for type '"
                  + type.name()
                  + "' key '"
                  + key
                  + "'");
        }
      } else {
        LOG.debug(
            "Failed to update '"
                + file.getAbsolutePath()
                + "' for type '"
                + type.name()
                + "' key '"
                + key
                + "'");
      }
    }
    return false;
  }

  /**
   * Deletes the requested data, if cacheOnly is false it will also delete the file
   *
   * @param type type / category of the entry
   * @param key key / ID of the entry
   * @param cacheOnly remove from cache-only for 'false' for a full delete
   */
  @Override
  public boolean delete(DataType type, String key, boolean cacheOnly) {
    if (cacheOnly) {
      return super.delete(type, key, cacheOnly);
    }
    File file = getFile(type, key);
    if (file.exists() && file.delete()) {
      LOG.debug(
          "Deleted file '" + file.getAbsolutePath() + "' type '" + type + "' key '" + key
              + "'");
      return true;
    }
    return false;
  }

  /**
   * Deletes there requested data from the cache
   *
   * @param type type / category of the entry
   * @param key key / ID of the entry
   * @see #delete(DataType, String, boolean)
   */
  @Override
  public boolean delete(DataType type, String key) {
    return delete(type, key, true);
  }
}
