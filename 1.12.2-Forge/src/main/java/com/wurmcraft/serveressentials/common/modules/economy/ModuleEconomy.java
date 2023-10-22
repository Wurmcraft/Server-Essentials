package com.wurmcraft.serveressentials.common.modules.economy;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.data_wrapper.PerkCost;
import com.wurmcraft.serveressentials.common.modules.economy.command.PerkCommand;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import joptsimple.internal.Strings;

@Module(name = "Economy")
public class ModuleEconomy {

  public void setup() {
    loadPerks();
  }

  public void reload() {}

  public static void loadPerks() {
    File perkDir = new File(SAVE_DIR + File.separator + "Storage" + File.separator + "perks");
    if (perkDir.exists()) {
      for (File file : perkDir.listFiles()) {
        try {
          PerkCost loadedPerk =
              ServerEssentials.GSON.fromJson(
                  Strings.join(Files.readAllLines(perkDir.toPath()), "\n"), PerkCost.class);
          PerkCommand.perks.put(loadedPerk.perkNode.toLowerCase(), loadedPerk);
        } catch (Exception e) {
          ServerEssentials.LOG.warn(
              "Failed to load perk '" + file.getPath() + "' (" + e.getMessage() + ")");
        }
      }
    } else {
      createDefaultPerks();
    }
  }

  public static void createDefaultPerks() {
    File perkDir = new File(SAVE_DIR + File.separator + "Storage" + File.separator + "perks");
    PerkCost home = new PerkCost(1.25, 1000, "home", 5);
    PerkCost vault = new PerkCost(2, 2500, "vault", 5);
    savePerk(new File(perkDir + File.separator + "home.json"), home);
    savePerk(new File(perkDir + File.separator + "vault.json"), vault);
  }

  private static void savePerk(File save, PerkCost perk) {
    if (!save.getParentFile().exists()) {
      save.getParentFile().mkdirs();
    }
    try {
      Files.write(
          save.toPath(), ServerEssentials.GSON.toJson(perk).getBytes(), StandardOpenOption.WRITE);
    } catch (Exception e) {
      ServerEssentials.LOG.warn(
          "Failed to save new perk '" + perk.perkNode + "' (" + e.getMessage() + ")");
      ServerEssentials.LOG.warn(ServerEssentials.GSON.toJson(perk));
    }
  }
}
