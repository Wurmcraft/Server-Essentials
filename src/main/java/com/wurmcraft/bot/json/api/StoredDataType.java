package com.wurmcraft.bot.json.api;


import com.wurmcraft.bot.json.api.json.JsonParser;

public interface StoredDataType extends JsonParser {

  /** @return ID of this given instance */
  String getID();
}
