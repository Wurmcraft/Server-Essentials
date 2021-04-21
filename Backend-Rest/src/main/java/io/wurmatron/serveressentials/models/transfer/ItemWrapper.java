package io.wurmatron.serveressentials.models.transfer;

import io.wurmatron.serveressentials.sql.SQLJson;

import java.util.Objects;

public class ItemWrapper implements SQLJson {

    public int count;
    public String item;
    public int meta;
    public String nbt;

    public ItemWrapper(String item) {
        // TODO Item Converter
        this.item = item;
        this.count = 0;
        this.meta = 0;
        this.nbt = null;
    }

    public ItemWrapper(int count, String item, int meta, String nbt) {
        this.count = count;
        this.item = item;
        this.meta = meta;
        this.nbt = nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemWrapper)) return false;
        ItemWrapper that = (ItemWrapper) o;
        return count == that.count && meta == that.meta && item.equals(that.item) && Objects.equals(nbt, that.nbt);
    }
}
