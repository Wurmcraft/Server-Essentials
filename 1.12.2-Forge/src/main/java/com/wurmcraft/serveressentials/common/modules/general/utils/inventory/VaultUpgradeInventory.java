package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Vault;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.FileDataLoader;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class VaultUpgradeInventory extends InventoryBasic {

    public ItemStack[] menu;
    public EntityPlayer player;
    public Language lang;
    public Vault vault;

    public VaultUpgradeInventory(EntityPlayer player, Language lang, Vault vault) {
        super(ChatHelper.replaceColor(lang.DISPLAY_VAULT.replaceAll("\\{@NAME@}", vault.name)), true, 54);
        this.player = player;
        this.lang = lang;
        this.vault = vault;
        menu = buildMenu();
    }

    private ItemStack[] buildMenu() {
        ItemStack[] items = new ItemStack[2];
        ItemStack EMPTY = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 8);
        EMPTY.setStackDisplayName("");
        items[0] = EMPTY;
        // Add Page
        ItemStack info = new ItemStack(Items.ITEM_FRAME, 1, 0);
        info.setStackDisplayName(ChatHelper.replaceColor(PlayerUtils.getLang(player).DISPLAY_VAULT_ADDPAGE) + " " + calculateUpgradeCost(vault.maxPages));
        NBTTagCompound lore = new NBTTagCompound();
        lore.setString("text", "" + calculateUpgradeCost(vault.maxPages));
        info.getOrCreateSubCompound("display").setTag("Lore", lore);
        items[1] = info;
        return items;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index == 22)
            return menu[1];
        return menu[0];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        handleAction(index);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    private void handleAction(int index) {
        if (index == 22) {
            if (EcoUtils.canBuy(((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency, calculateUpgradeCost(vault.maxPages), PlayerUtils.getLatestAccount(player.getGameProfile().getId().toString()))) {
                vault.maxPages = vault.maxPages + 1;
                saveVault();
                ChatHelper.send(player, PlayerUtils.getLang(player).COMMAND_VAULT_UPGRADED);
                closeInventory(player);
                player.closeScreen();
                player.displayGUIChest(
                        new VaultInventory(player, lang, VaultInventory.loadVault(vault.ownerUUID, vault.name), 0));
            } else {
                ChatHelper.send(player, PlayerUtils.getLang(player).COMMAND_BALANCE_EMPTY);
            }
        }
    }

    public void saveVault() {
        try {
            File save =
                    new File(
                            ConfigLoader.SAVE_DIR
                                    + File.separator
                                    + FileDataLoader.SAVE_FOLDER
                                    + File.separator
                                    + "vaults"
                                    + File.separator
                                    + vault.ownerUUID
                                    + File.separator
                                    + vault.name
                                    + ".json");
            if (!save.exists()) {
                save.getParentFile().mkdirs();
                save.createNewFile();
            }
            Files.write(
                    save.toPath(),
                    GSON.toJson(vault).getBytes(),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            LOG.warn("Failed to save vault '{}' for user '{}'", vault.name, vault.ownerUUID);
        }
    }

    public static double calculateUpgradeCost(int maxPage) {
        return ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).vaultPageBaseCost + (((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).vaultPageCostMultiplier * maxPage);
    }
}
