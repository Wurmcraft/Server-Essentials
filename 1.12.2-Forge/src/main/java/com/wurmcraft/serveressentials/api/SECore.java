package com.wurmcraft.serveressentials.api;

import com.wurmcraft.serveressentials.common.data.loader.IDataLoader;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class SECore {

    public static NonBlockingHashMap<String, Object> modules = new NonBlockingHashMap<>();
    public static IDataLoader dataLoader;

}
