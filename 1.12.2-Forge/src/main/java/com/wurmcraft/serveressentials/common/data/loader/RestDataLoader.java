package com.wurmcraft.serveressentials.common.data.loader;

import com.google.gson.JsonParseException;
import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.models.AuthUser;
import com.wurmcraft.serveressentials.api.models.MessageResponse;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class RestDataLoader extends FileDataLoader {

    public static final String KEY_SEPARATOR = ";";

    protected static final NonBlockingHashMap<String, String> fieldToQuery = new NonBlockingHashMap<>();
    private long expiration = -1;

    static {
        // Action
        fieldToQuery.put("relatedID", "related-id");
        fieldToQuery.put("actionData", "action-data");
        // Account
        fieldToQuery.put("muteTime", "mute-time");
        fieldToQuery.put("displayName", "display-name");
        fieldToQuery.put("discordID", "discord-id");
        fieldToQuery.put("trackedTime", "tracked-time");
        fieldToQuery.put("rewardPoints", "reward-points");
        fieldToQuery.put("passwordHash", "password-hash");
        fieldToQuery.put("passwordSalt", "password-salt");
        fieldToQuery.put("systemPerms", "system-perms");
        // Ban
        fieldToQuery.put("bannedBy", "banned-by");
        fieldToQuery.put("bannedByType", "banned-by-type");
        fieldToQuery.put("banReason", "ban-reason");
        fieldToQuery.put("banType", "ban-type");
        fieldToQuery.put("banData", "ban-data");
        fieldToQuery.put("banStatus", "ban-status");
        // Currency
        fieldToQuery.put("currencyID", "currency-id");
        fieldToQuery.put("globalWorth", "global-worth");
        fieldToQuery.put("sellWorth", "sell-worth");
        // Donator
        fieldToQuery.put("transactionID", "transaction-id");
        fieldToQuery.put("typeData", "type-data");
        // Log Entry
        fieldToQuery.put("serverID", "server-id");
        fieldToQuery.put("actionType", "action-type");
        // Market Entry
        fieldToQuery.put("sellerUUID", "seller-uuid");
        fieldToQuery.put("currencyName", "currency-name");
        fieldToQuery.put("currencyAmount", "currency-amount");
        fieldToQuery.put("marketType", "market-type");
        fieldToQuery.put("marketData", "market-data");
        fieldToQuery.put("transferID", "transfer-id");
        // Rank
        fieldToQuery.put("rankID", "rank-id");
        fieldToQuery.put("prefixPriority", "prefix-priority");
        fieldToQuery.put("suffixPriority", "suffix-priority");
        fieldToQuery.put("colorPriority", "color-priority");
        // Transfer Entry
        fieldToQuery.put("startTime", "start-time");
        SAVE_FOLDER = "Cache";
    }

    private boolean login(String path) {
        try {
            RequestGenerator.HttpResponse response = RequestGenerator.post(path, new AuthUser("Server", ServerEssentials.config.general.serverID, new String[0], ServerEssentials.config.storage.token, ServerEssentials.config.storage.key, expiration));
            if (isValidResponse(response)) {
                AuthUser user = GSON.fromJson(response.response, AuthUser.class);
                RequestGenerator.token = user.token;
                expiration = user.expiration;
                return true;
            } else {
                LOG.fatal("Failed to authenticate with Rest API");
                handleResponseError(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.fatal("Failed to login to Rest API");
        }
        return false;
    }

    public boolean reauth() {
        return login("api/reauth");
    }

    public boolean login() {
        return login("api/login");
    }

    /**
     * Checks if the cache contains the files, if not pull from rest, ignore files (may be outdated)
     *
     * @param key  type of data you are looking for
     * @param type cast the data to this type
     */
    @Override
    public <T> NonBlockingHashMap<String, T> getFromKey(DataType key, T type) {
        if (storage.containsKey(key) || key.path == null)
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
                                updateOrCreateFileCache(key, dataKey, d);
                                cache(key, dataKey, d);
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

    /**
     * Use reflection to find the key of the requested data, based on the provided type
     *
     * @param type key / category of the data
     * @param data instance of the data to reflect upon
     */
    private String getKey(DataType type, Object data) {
        try {
            Field field = data.getClass().getDeclaredField(type.key);
            Object instance = field.get(data);
            if (instance instanceof String)
                return (String) instance;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("Failed to find key for '" + type.name() + "'");
        }
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
        // Check in-cache
        if (storage.containsKey(type) && storage.get(type).containsKey(key)) {
            Object[] data = storage.get(type).get(key);
            if (((long) data[0]) > System.currentTimeMillis()) {
                return storage.get(type).get(key)[1];
            } else
                storage.get(type).remove(key);
        }
        if (type.path == null)
            return super.get(type, key);
        // Not in cache
        try {
            RequestGenerator.HttpResponse response = findAndExecutePath(type, key);
            if (isValidResponse(response)) {
                Object data = GSON.fromJson(response.response, type.instanceType);
                cache(type, key, data);
            } else {
                handleResponseError(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("Failed to get from API '" + findPath(type, key));
        }
        return null;
    }

    private String getKey(Object data) {
        for (DataType type : DataType.values())
            if (type.instanceType.getDeclaringClass().equals(data.getClass())) {
                if (type.key != null) {
                    try {
                        Field field = data.getClass().getDeclaredField(type.key);
                        return (String) field.get(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.debug("Failed to get key for '" + data.getClass().getSimpleName() + "'");
                    }
                } else {    // Combo Key, generated via pathType
                    StringBuilder builder = new StringBuilder();
                    String[] queryParams = type.pathType.split(";");
                    for (int index = 0; index < queryParams.length; index++) {
                        try {
                            Field field = data.getClass().getDeclaredField(queryParams[index]);
                            builder.append(field.get(data)).append(";");
                            String generatedKey = builder.toString();
                            return generatedKey.substring(0, generatedKey.length() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.debug("Failed to get key for '" + data.getClass().getSimpleName() + "' (" + queryParams[index] + ")");
                        }
                    }
                }
            }
        return null;
    }

    private RequestGenerator.HttpResponse findAndExecutePath(DataType type, String key) throws IOException {
        String path = findPath(type, key);
        if (!type.path.equals(path))
            return RequestGenerator.get(path);
        else {
            String[] keys = key.split(";");
            Map<String, String> query = new HashMap<>();
            String[] queryKeys = type.pathType.split(";");
            for (int index = 0; index < queryKeys.length; index++)
                query.put(fieldToQuery.get(queryKeys[index]), keys[index]);
            return RequestGenerator.get(path, query);
        }
    }


    private String findPath(DataType type, String key) {
        if (type.pathType.contains("CRUD"))
            return type.path + "/" + key;
        return type.path;
    }

    @Override
    public <T> T get(DataType type, String key, T data) {
        Object obj = get(type, key);
        if (obj != null)
            return (T) obj;
        return null;
    }

    @Override
    public boolean register(DataType type, String key, Object data) {
        if (storage.containsKey(type) && storage.get(type).containsKey(key))
            return false;
        if (type.path == null)
            return super.register(type, key, data);
        // New / Register / Send to Rest API
        try {
            RequestGenerator.HttpResponse response = RequestGenerator.post(type.path, data);
            if (isValidResponse(response)) {
                cache(type, getKey(data), data);
                if (type.fileCache)
                    updateOrCreateFileCache(type, getKey(data), data);
                return true;
            } else
                handleResponseError(response);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.debug("Failed to post '" + type.path + "' for '" + key + "'");
            LOG.debug("JSON: " + GSON.toJson(data));
        }
        return false;
    }

    @Override
    public boolean update(DataType type, String key, Object data) {
        if (!storage.containsKey(type) || !storage.get(type).containsKey(key))
            return false;
        if(type.path == null)
            return super.update(type,key,data);
        // Existing in cache, updating
        try {
            String path = type.path;
            if (type.key != null)
                path = path + "/" + key;
            RequestGenerator.HttpResponse response = RequestGenerator.put(path, data);
            if (isValidResponse(response)) {
                cache(type, getKey(data), data);
                if (type.fileCache)
                    updateOrCreateFileCache(type, getKey(data), data);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.debug("Failed to put '" + type.path + "' for '" + key + "'");
            LOG.debug("JSON: " + GSON.toJson(data));
        }
        return false;
    }

    @Override
    public boolean delete(DataType type, String key, boolean cacheOnly) {
        if (cacheOnly || type.path == null)
            return super.delete(type, key, cacheOnly);
        try {
            String path = type.path;
            if (type.key != null)
                path = path + "/" + key;
            Object obj = get(type, key);
            RequestGenerator.HttpResponse response = RequestGenerator.delete(path, obj);
            if (isValidResponse(response))
                return super.delete(type, key, cacheOnly);
            else
                handleResponseError(response);
        } catch (IOException e) {
            LOG.debug("Failed to delete '" + type.path + "' for '" + key + "'");
        }
        return false;
    }

    @Override
    public boolean delete(DataType type, String key) {
        return delete(type, key, true);
    }
}
