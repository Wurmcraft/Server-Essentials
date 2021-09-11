package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.Vault;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.FileDataLoader;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.VaultInventory;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

import java.io.File;
import java.nio.file.Files;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

@ModuleCommand(module = "General", name = "Vault", defaultAliases = {"Storage"})
public class VaultCommand {

    public static final String[] INVALID_VAULT_NAMES = new String[]{"list", "create", "make", "c", "delete", "del", "d", "remove", "rem", "r"};

    @Command(args = {}, usage = {})
    public void defaultVault(ServerPlayer player) {
        vault(player, ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).defaultVaultName);
    }

    @Command(args = {CommandArgument.STRING}, usage = {"name"})
    public void vault(ServerPlayer player, String vaultName) {
        if (vaultName.equalsIgnoreCase("list")) {
            File dir = getVaultLocation(player.player.getGameProfile().getId().toString(), "test").getParentFile();
            StringBuilder builder = new StringBuilder();
            for (String f : dir.list())
                builder.append(f.replaceAll(".json", "")).append(", ");
            ChatHelper.send(player.sender, builder.toString());
        } else {
            Vault vault = getVault(player.player.getGameProfile().getId().toString(), vaultName);
            // TODO Check for expiration
            if (vault != null) {
                player.player.displayGUIChest(new VaultInventory(player.player, player.lang, vault, 0));
            } else
                ChatHelper.send(player.sender, player.lang.COMMAND_VAULT_NONE.replaceAll("\\{@NAME@}", vaultName));
        }
    }

    @Command(args = {CommandArgument.STRING, CommandArgument.STRING}, usage = {"arg", "name"})
    public void manageVault(ServerPlayer player, String arg, String name) {
        if (arg.equalsIgnoreCase("create") || arg.equalsIgnoreCase("make") || arg.equalsIgnoreCase("c")) {
            for (String check : INVALID_VAULT_NAMES)
                if (name.equalsIgnoreCase(check)) {
                    ChatHelper.send(player.sender, player.lang.COMMAND_VAULT_INVALID);
                    return;
                }
            // TODO Check max vault count
            Vault vault = new Vault(player.player.getGameProfile().getId().toString(), name, 2);
            player.player.displayGUIChest(new VaultInventory(player.player, player.lang, vault, 0));
        } else if (arg.equalsIgnoreCase("delete") || arg.equalsIgnoreCase("del") || arg.equalsIgnoreCase("d") || arg.equalsIgnoreCase("remove") || arg.equalsIgnoreCase("rem") || arg.equalsIgnoreCase("r")) {
            Vault vault = getVault(player.player.getGameProfile().getId().toString(), name);
            if (vault != null) {
                File save = getVaultLocation(player.player.getGameProfile().getId().toString(), name);
                // TODO Send Vault contents to storage
                if (save.delete())
                    ChatHelper.send(player.sender, player.lang.COMMAND_VAULT_DELETE.replaceAll("\\{@NAME@}", name));
            }
        }
    }

    public static Vault getVault(String ownerUUID, String name) {
        File file = getVaultLocation(ownerUUID, name);
        if (file.exists()) {
            try {
                return GSON.fromJson(String.join("\n", Files.readAllLines(file.toPath())), Vault.class);
            } catch (Exception e) {
                LOG.warn("Failed to load vault '" + name + "' for '" + ownerUUID + "'");
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getVaultLocation(String ownerUUID, String name) {
        return new File(ConfigLoader.SAVE_DIR + File.separator + FileDataLoader.SAVE_FOLDER + File.separator + "vaults" + File.separator + ownerUUID + File.separator + name + ".json");
    }
}