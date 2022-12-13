package com.wurmcraft.serveressentials.common.modules.security.command;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.security.ConfigSecurity;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.File;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;

@ModuleCommand(module = "Security", name = "Lockdown")
public class LockdownCommand {

  @Command(args = {}, usage = {}, canConsoleUse = true)
  public void lockdown(ServerPlayer player) {
    ConfigSecurity securityLock = (ConfigSecurity) SECore.moduleConfigs.get("SECURITY");
    if (securityLock.lockdownEnabled) {
      securityLock.lockdownEnabled = false;
      for (EntityPlayer p : FMLClientHandler.instance().getServer().getPlayerList()
          .getPlayers()) {
        Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT, p.getGameProfile().getId().toString(),
                new Account()).lang, new Language());
        ChatHelper.send(p, lang.COMMAND_LOCKDOWN_DISABLED);
      }
      File configFile =
          new File(
              SAVE_DIR + File.separator + "Modules" + File.separator + "Security.json");
      ConfigLoader.save(configFile, securityLock);
    } else {
      securityLock.lockdownEnabled = true;
      for (EntityPlayer p : FMLClientHandler.instance().getServer().getPlayerList()
          .getPlayers()) {
        Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT, p.getGameProfile().getId().toString(),
                new Account()).lang, new Language());
        ChatHelper.send(p, lang.COMMAND_LOCKDOWN_ENABLED);
      }
      File configFile =
          new File(
              SAVE_DIR + File.separator + "Modules" + File.separator + "Security.json");
      ConfigLoader.save(configFile, securityLock);
    }
  }
}
