package com.wurmcraft.serveressentials.common.modules.economy;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Currency;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.data_wrapper.PerkCost;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.donation.ConfigDonation;
import com.wurmcraft.serveressentials.common.modules.economy.command.PerkCommand;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import joptsimple.internal.Strings;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

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
      if (save.getParentFile().mkdirs()) ServerEssentials.LOG.debug("Creating perk directory");
    }
    try {
      Files.write(
          save.toPath(), ServerEssentials.GSON.toJson(perk).getBytes(), StandardOpenOption.WRITE);
    } catch (Exception e) {
      ServerEssentials.LOG.warn("Failed to save new perk '{}' ({})", perk.perkNode, e.getMessage());
      ServerEssentials.LOG.warn("JSON: {}", ServerEssentials.GSON.toJson(perk));
    }
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigEconomy cfg = (ConfigEconomy) SECore.moduleConfigs.get("ECONOMY");
    info.add("serverCurrency: " + cfg.serverCurrency);
    info.add("defaultMaxListings: " + cfg.defaultMaxListings);
    info.add("itemBlacklist: " + Strings.join(cfg.itemBlacklist, ", "));
    info.add("maxListingsPerCommand: " + cfg.maxListingsPerCommand);
    info.add("taxes>bankTaxMultiplayer: " + cfg.taxes.bankTaxMultiplayer);
    info.add("taxes>commandTaxMultiplayer: " + cfg.taxes.commandTaxMultiplayer);
    info.add("taxes>incomeTaxMultiplayer: " + cfg.taxes.incomeTaxMultiplayer);
    info.add("taxes>payTaxMultiplayer: " + cfg.taxes.payTaxMultiplayer);
    info.add("taxes>salesTaxMultiplayer: " + cfg.taxes.salesTaxMultiplayer);
    info.add("market>entries: " + (MarketHelper.entries != null ? String.valueOf(MarketHelper.entries.size()) : "nil"));
    List<String> curreneies = new ArrayList<>();
    NonBlockingHashMap<String, Currency> a = SECore.dataLoader.getFromKey(DataLoader.DataType.CURRENCY, new Currency());
    if(a != null)
    for(Currency cur : a.values())
      curreneies.add("<" + cur.display_name + "$" + cur.global_worth + "-" + cur.sell_worth + "@" + cur.tax + ">");
    info.add("Currencies: ("  + curreneies.size() + ")"  + Strings.join(curreneies, ", "));
    return info.toArray(new String[0]);
  }
}
