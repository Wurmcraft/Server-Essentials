package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;

import java.util.Objects;

public class MarketEntry {
    public String server_id;
    public String seller_uuid;
    public ItemWrapper item;
    public String currency_name;
    public Double currency_amount;
    public Long timestamp;
    public String market_type;
    public String market_data;
    public String transfer_id;

    /**
     * @param serverID       id of the server, where the trade started
     * @param sellerUUID     uuid of the seller
     * @param item           json data of the item to be sold
     * @param currencyName   name of the currency being used by the entry
     * @param currencyAmount current amount of the currency being used by this entry
     * @param timestamp      unix timestamp when the entry was created
     * @param marketType     type of the market for this entry
     * @param marketData     data related to the market entry
     * @param transferID     server's transferID, for use with multi-server market's
     */
    public MarketEntry(String serverID, String sellerUUID, ItemWrapper item, String currencyName, double currencyAmount, long timestamp, String marketType, String marketData, String transferID) {
        this.server_id = serverID;
        this.seller_uuid = sellerUUID;
        this.item = item;
        this.currency_name = currencyName;
        this.currency_amount = currencyAmount;
        this.timestamp = timestamp;
        this.market_type = marketType;
        this.market_data = marketData;
        this.transfer_id = transferID;
    }

    public MarketEntry() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketEntry)) return false;
        MarketEntry that = (MarketEntry) o;
        return Objects.equals(server_id, that.server_id) && Objects.equals(seller_uuid, that.seller_uuid) && Objects.equals(item, that.item) && Objects.equals(currency_name, that.currency_name) && Objects.equals(currency_amount, that.currency_amount) && Objects.equals(timestamp, that.timestamp) && Objects.equals(market_type, that.market_type) && Objects.equals(market_data, that.market_data) && Objects.equals(transfer_id, that.transfer_id);
    }

    @Override
    public MarketEntry clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, MarketEntry.class);
    }
}
