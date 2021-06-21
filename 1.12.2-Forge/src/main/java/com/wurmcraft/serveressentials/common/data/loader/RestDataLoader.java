package com.wurmcraft.serveressentials.common.data.loader;

import com.google.gson.JsonParseException;
import com.wurmcraft.serveressentials.api.models.MessageResponse;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class RestDataLoader extends FileDataLoader {

    /**
     * Checks if the cache contains the files, if not pull from rest, ignore files (may be outdated)
     *
     * @param key  type of data you are looking for
     * @param type cast the data to this type
     */
    @Override
    public <T> NonBlockingHashMap<String, T> getFromKey(DataType key, T type) {
        if (storage.containsKey(key))
            return super.getFromKey(key, type);
        else {
            try {
                RequestGenerator.HttpResponse response = RequestGenerator.get(key.path);
                if (isValidResponse(response)) {
                    // Yes, this is the best way i found to convert class -> class[] (j8)
                    // Object[] data = GSON.fromJson(response.response,  key.instanceType.arrayType());
                    Object[] data = GSON.fromJson(response.response, Collections.singletonList(key.instanceType).toArray().getClass());
                    if (data != null)
                        for (Object d : data) {
                            String dataKey = getKey(key, d);
                            if (dataKey != null && !dataKey.isEmpty()) {
                                cache(key, dataKey, d);
                                updateOrCreateFileCache(key, dataKey, d);
                                return super.getFromKey(key, type);
                            } else {
                                LOG.debug("Json: " + GSON.toJson(d));
                                LOG.debug("Failed to find key for '" + key + "'");
                            }
                        }
                } else
                    handleResponseError(response);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("Failed to read from endpoint '" + key.path + "' with type '" + key.pathType + "'");
            }
            return new NonBlockingHashMap<>();
        }
    }

    private String getKey(DataType type, Object data) {
        return null;
    }

    private boolean isValidResponse(RequestGenerator.HttpResponse response) {
        return response.status == 200 || response.status == 201;
    }

    private void handleResponseError(RequestGenerator.HttpResponse response) {
        // Client Error
        if (response.status >= 400 && response.status <= 499) {
            try {
                MessageResponse[] errors = GSON.fromJson(response.response, MessageResponse[].class);
                LOG.debug("Error Status: " + response.status);
                for (MessageResponse error : errors)
                    LOG.debug("Error: " + error.title + " (" + error.message + ")");
            } catch (JsonParseException e) {
                e.printStackTrace();
                LOG.warn("Failed to parse an error from an endpoint  '" + response.status + "' (" + e.getMessage() + ")");
                LOG.warn("Response: " + response.response);
            }
        }
        // Server Error
        if (response.status >= 500 && response.status <= 599) {
            MessageResponse error = GSON.fromJson(response.response, MessageResponse.class);
            LOG.debug("Error Status: " + response.status);
            LOG.debug("Error: " + error.title + " (" + error.message + ")");
        }
    }

    private void updateOrCreateFileCache(DataType type, String key, Object data) {
        if (type.fileCache && key != null && !key.isEmpty()) {
            File file = getFile(type, key);
            if (file.exists()) // Update existing entry
                super.update(type, key, data);
            else    // New entry
                super.register(type, key, data);
        }
    }

    @Nullable
    @Override
    public Object get(DataType type, String key) {
        return super.get(type, key);
    }

    @Override
    public <T> T get(DataType type, String key, T data) {
        return super.get(type, key, data);
    }

    @Override
    public boolean register(DataType type, String key, Object data) {
        return super.register(type, key, data);
    }

    @Override
    public boolean update(DataType type, String key, Object data) {
        return super.update(type, key, data);
    }

    @Override
    public boolean delete(DataType type, String key, boolean cacheOnly) {
        return super.delete(type, key, cacheOnly);
    }

    @Override
    public boolean delete(DataType type, String key) {
        return super.delete(type, key);
    }
}
