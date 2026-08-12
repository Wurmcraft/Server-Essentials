package com.wurmcraft.serveressentials.common.modules.transfer.utils;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.TransferEntry;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransferUtils {

  public static TransferEntry[] get(String transferID, String uuid) {
    TransferEntry[] data =
        SECore.dataLoader.get(DataLoader.DataType.TRANSFER, transferID, new TransferEntry[0]);
    List<TransferEntry> entries = new ArrayList<>();
    if (uuid == null || uuid.isEmpty()) return data;
    for (TransferEntry entry : data) if (Objects.equals(entry.uuid, uuid)) entries.add(entry);
    return entries.toArray(new TransferEntry[0]);
  }

  public static boolean update(TransferEntry entry) {
    return SECore.dataLoader.update(
        DataLoader.DataType.TRANSFER, String.valueOf(entry.transfer_id), entry);
  }
}
