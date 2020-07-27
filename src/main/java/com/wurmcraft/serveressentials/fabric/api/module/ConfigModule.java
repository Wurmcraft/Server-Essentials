package com.wurmcraft.serveressentials.fabric.api.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Automatically create a config file based on the class. For this annotation to function the class
 * is required to be a subclass of JsonParser along with 2 constructors one empty and one with all
 * the values. The default constructor is used to set the default values.
 *
 * @see com.wurmcraft.serveressentials.fabric.api.json.JsonParser
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigModule {

  /** Name of the module this config file belongs to. */
  String moduleName();
}