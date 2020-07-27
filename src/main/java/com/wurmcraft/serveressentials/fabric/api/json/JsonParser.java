package com.wurmcraft.serveressentials.fabric.api.json;

/**
 * Used in combination with the Gson library to convert Files/Strings to java class
 * instances.
 */
public interface JsonParser {

  /**
   *  Used to determine how its stored,
   *  File: <ID>.json
   *  Map (Key,Value): (ID, class instance)
   *
   * @return ID / name of the data
   */
  String getID();

}
