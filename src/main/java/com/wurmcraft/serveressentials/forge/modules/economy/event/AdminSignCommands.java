package com.wurmcraft.serveressentials.forge.modules.economy.event;

import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.SignUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.SignUtils.SignType;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.StackConverter;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AdminSignCommands {

  public static String NBT_DATA = "SE-DATA";

  @SubscribeEvent
  public void onRightClick(PlayerInteractEvent.RightClickBlock e) { // Use / Active Sign
    if (e.getWorld().getBlockState(e.getPos()).getBlock() instanceof BlockSign) {
      TileEntitySign sign = (TileEntitySign) e.getWorld().getTileEntity(e.getPos());
      if (sign.getTileData().hasKey(NBT_DATA)) {
        SignType type = SignUtils.getType(e.getWorld(), e.getPos());
        if (type != null && type.equals(SignType.ADMIN_BUY)) {
          double cost = Double.parseDouble(sign.signText[3].getUnformattedText());
          if (EcoUtils.hasCurrency(e.getEntityPlayer(), cost)) {
            if (e.getEntityPlayer().inventory.addItemStackToInventory(
                StackConverter.getData(sign.getTileData().getString(NBT_DATA)))) {
              EcoUtils.consumeCurrency(e.getEntityPlayer(), cost);
              ChatHelper.sendMessage(e.getEntityPlayer(),
                  PlayerUtils.getLanguage(e.getEntityPlayer()).SIGN_BUY
                      .replaceAll("%AMOUNT%", cost + ""));
            } else {
              ChatHelper.sendMessage(e.getEntityPlayer(),
                  PlayerUtils.getLanguage(e.getEntityPlayer()).SIGN_INVENTORY_FULL);
            }
          } else {
            ChatHelper.sendMessage(e.getEntityPlayer(),
                PlayerUtils.getLanguage(e.getEntityPlayer()).ECO_MONEY_INSUFFICENT
                    .replaceAll("%AMOUNT%", "" + cost));
          }
        } else if (type != null && type.equals(SignType.ADMIN_SELL)) {
          ItemStack stack = StackConverter
              .getData(sign.getTileData().getString(NBT_DATA));
          if (hasItemStack(e.getEntityPlayer(), stack)) {
            consumeStack(e.getEntityPlayer(), stack);
            ChatHelper.sendMessage(e.getEntityPlayer(),
                PlayerUtils.getLanguage(e.getEntityPlayer()).SIGN_SELL);
            double money = Double.parseDouble(sign.signText[3].getUnformattedText());
            EcoUtils.addCurrency(e.getEntityPlayer(), money);
          } else {
            ChatHelper.sendMessage(e.getEntityPlayer(),
                PlayerUtils.getLanguage(e.getEntityPlayer()).SIGN_SELL_EMPTY);
          }
        }
      }
    }
  }

  private static boolean hasItemStack(EntityPlayer player, ItemStack stack) {
    int totalNeeded = stack.getCount();
    for (ItemStack item : player.inventory.mainInventory) {
      if (item.isItemEqual(stack)) {
        totalNeeded -= item.getCount();
        if (totalNeeded <= 0) {
          return true;
        }
      }
    }
    return totalNeeded <= 0;
  }

  private static void consumeStack(EntityPlayer player, ItemStack stack) {
    int leftToRemove = stack.getCount();
    for (int index = 0; index < player.inventory.mainInventory.size(); index++) {
      ItemStack item = player.inventory.getStackInSlot(index);
      if (item.isItemEqual(stack)) {
        if(leftToRemove > item.getCount()) {
          leftToRemove -= item.getCount();
          player.inventory.setInventorySlotContents(index, ItemStack.EMPTY);
        } else {
          leftToRemove -= item.getCount();
          item.setCount(item.getCount() - leftToRemove);
          player.inventory.setInventorySlotContents(index, item);
        }
      }
    }
  }


  @SubscribeEvent
  public void onLeftClick(PlayerInteractEvent.LeftClickBlock e) { // Break / Setup-Active
    if (e.getWorld().getBlockState(e.getPos()).getBlock() instanceof BlockSign) {
      TileEntitySign sign = (TileEntitySign) e.getWorld().getTileEntity(e.getPos());
      if (!sign.signText[0].getUnformattedText().isEmpty() && SignUtils
          .isValidSign(e.getWorld(), e.getPos())) {
        SignType type = SignUtils.getType(e.getWorld(), e.getPos());
        if (!e.getEntityPlayer().isSneaking()) {
          e.setCanceled(true);
        }
        if (type != null) {
          if (type.equals(SignType.ADMIN_BUY)) {
            handleAdminBuy(e.getEntityPlayer(), sign);
          } else if (type.equals(SignType.ADMIN_SELL)) {
            handleAdminSell(e.getEntityPlayer(), sign);
          }
        }
      }
    }
  }

  public void handleAdminBuy(EntityPlayer player, TileEntitySign sign) {
    Double.parseDouble(sign.signText[3].getUnformattedText());
    ItemStack stack = player.getHeldItemMainhand();
    if (!stack.isEmpty()) {
      if (RankUtils.hasPermission(player, "economy.signAdmin.create")) {
        sign.signText[0].getStyle().setColor(TextFormatting.RED);
        sign.signText[1].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[2].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[3].getStyle().setColor(TextFormatting.GOLD);
        sign.getTileData().setString(NBT_DATA, StackConverter.toString(stack));
        sign.markDirty();
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper
            .sendHoverMessage(player, noPerms,
                TextFormatting.RED + "economy.signAdmin.create");
      }
    } else {
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).SIGN_NO_ITEM);
    }
  }

  public void handleAdminSell(EntityPlayer player, TileEntitySign sign) {
    Double.parseDouble(sign.signText[3].getUnformattedText());
    ItemStack stack = player.getHeldItemMainhand();
    if (!stack.isEmpty()) {
      if (RankUtils.hasPermission(player, "economy.signAdmin.create")) {
        sign.signText[0].getStyle().setColor(TextFormatting.RED);
        sign.signText[1].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[2].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[3].getStyle().setColor(TextFormatting.GOLD);
        sign.getTileData().setString(NBT_DATA, StackConverter.toString(stack));
        sign.markDirty();
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper
            .sendHoverMessage(player, noPerms,
                TextFormatting.RED + "economy.signAdmin.create");
      }
    } else {
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).SIGN_NO_ITEM);
    }
  }
}
