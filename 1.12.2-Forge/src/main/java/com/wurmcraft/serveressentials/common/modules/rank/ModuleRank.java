package com.wurmcraft.serveressentials.common.modules.rank;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;

@Module(name = "Rank", dependencies = {"Core"})
public class ModuleRank {

    public void setup() {
        try {
            if (SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()) == null || (SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()).size() <= 0))
                setupDefaultRanks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        for (String rank : SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()).keySet())
            SECore.dataLoader.delete(DataLoader.DataType.RANK, rank, true);
    }

    public void setupDefaultRanks() {
        Rank defaultRank = new Rank(((ConfigRank) SECore.moduleConfigs.get("RANK")).defaultRank, new String[]{"command.help", "command.home", "command.sethome", "command.tpa", "command.tpaccept", "command.tpadeny", "command.spawn"}, new String[]{}, "&8[&7Default&8]", 0, "&7", 0, "&3", 0);
        Rank member = new Rank("Member", new String[]{"command.warp", "command.enderchest"}, new String[]{((ConfigRank) SECore.moduleConfigs.get("RANK")).defaultRank}, "&8[&eMember&8]", 1, "&7", 1, "&b", 1);
        Rank admin = new Rank("Admin", new String[]{"*"}, new String[]{"Member"}, "&c[&4Admin&c]", 2, "&7", 2, "&b", 2);
        if (SECore.dataLoader.get(DataLoader.DataType.RANK, defaultRank.name) == null)
            SECore.dataLoader.register(DataLoader.DataType.RANK, defaultRank.name, defaultRank);
        if (SECore.dataLoader.get(DataLoader.DataType.RANK, member.name) == null)
            SECore.dataLoader.register(DataLoader.DataType.RANK, member.name, member);
        if (SECore.dataLoader.get(DataLoader.DataType.RANK, admin.name) == null)
            SECore.dataLoader.register(DataLoader.DataType.RANK, admin.name, admin);
    }
}
