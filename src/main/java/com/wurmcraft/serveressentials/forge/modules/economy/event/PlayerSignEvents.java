package com.wurmcraft.serveressentials.forge.modules.economy.event;

import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.SignUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.SignUtils.SignType;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.StackConverter;
import java.util.Objects;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class PlayerSignEvents {

  public static NonBlockingHashMap<EntityPlayer, BlockPos> activeSetup = new NonBlockingHashMap<>();
  public static String OWNER_DATA = "Owner";
  public static String ITEM_DATA = "SignItem";
  public static String CHEST_DATA = "LinkLocation";

  @SubscribeEvent
  public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
    if (e.getWorld().getTileEntity(e.getPos()) instanceof TileEntitySign) {
      TileEntitySign sign = (TileEntitySign) e.getWorld().getTileEntity(e.getPos());
      SignType type = SignUtils.getType(e.getWorld(), e.getPos());
      if (type != null && sign != null && sign.getTileData().hasKey(ITEM_DATA)) {
        int[] pos = sign.getTileData().getIntArray(CHEST_DATA);
        if (pos.length < 2 || sign.getTileData().getString(OWNER_DATA).isEmpty()) {
          return;
        }
        IInventory inv = (IInventory) e.getWorld()
            .getTileEntity(new BlockPos(pos[0], pos[1], pos[2]));
        ItemStack signStack = StackConverter
            .getData(sign.getTileData().getString(ITEM_DATA));
        if (type.equals(SignType.Buy)) {
          double cost = Double.parseDouble(sign.signText[3].getUnformattedText());
          if (EcoUtils.hasCurrency(e.getEntityPlayer(), cost)) {
            if (consumeStack(inv, signStack)) {
              EcoUtils.consumeCurrency(e.getEntityPlayer(), cost);
              ChatHelper.sendMessage(e.getEntityPlayer(),
                  PlayerUtils.getLanguage(e.getEntityPlayer()).SIGN_BUY
                      .replaceAll("%AMOUNT%", cost + ""));
              EcoUtils.addOfflineCurrency(sign.getTileData().getString(OWNER_DATA),
                  EconomyModule.config.defaultCurrency.name, cost);
              e.getEntityPlayer().inventory.addItemStackToInventory(signStack);
            } else {
              ChatHelper.sendMessage(e.getEntityPlayer(), PlayerUtils.getLanguage(e.getEntityPlayer()).SIGN_EMPTY);
            }
          } else {
            ChatHelper.sendMessage(e.getEntityPlayer(),
                PlayerUtils.getLanguage(e.getEntityPlayer()).ECO_MONEY_INSUFFICENT
                    .replaceAll("%AMOUNT%", "" + cost));
          }
        } else if (type.equals(SignType.SELL)) {
          double cost = Double.parseDouble(sign.signText[3].getUnformattedText());

          }
      }
    }
  }

  private static boolean consumeStack(IInventory inv, ItemStack stack) {
    int amountLeftToRemove = stack.getCount();
    for (int index = 0; index < stack.getCount(); index++) {
      ItemStack slotStack = inv.getStackInSlot(index);
      if (slotStack.getCount() > amountLeftToRemove) {
        slotStack.setCount(slotStack.getCount() - amountLeftToRemove);
        inv.setInventorySlotContents(index, slotStack);
        return true;
      } else {
        amountLeftToRemove -= slotStack.getCount();
        inv.setInventorySlotContents(index, ItemStack.EMPTY);
      }
    }
    return amountLeftToRemove <= 0;
  }

  @SubscribeEvent
  public void onLeftClick(PlayerInteractEvent.LeftClickBlock e) { // Break / Setup-Active
    if (e.getWorld().getTileEntity(e.getPos()) instanceof TileEntitySign) {
      TileEntitySign sign = (TileEntitySign) e.getWorld().getTileEntity(e.getPos());
      if (!sign.signText[0].getUnformattedText().isEmpty() && SignUtils
          .isValidSign(e.getWorld(), e.getPos())) {
        SignType type = SignUtils.getType(e.getWorld(), e.getPos());
        if (type != null) {
          if (type.equals(SignType.Buy)) {
            handleBuy(e.getEntityPlayer(), sign);
          } else if (type.equals(SignType.SELL)) {
            handleSell(e.getEntityPlayer(), sign);
          }
        }
      }
    } else if (e.getWorld().getTileEntity(e.getPos()) instanceof IInventory) {
      IInventory inventory = (IInventory) e.getWorld().getTileEntity(e.getPos());
      if (e.getEntityPlayer().getHeldItemMainhand().isEmpty()) {
        boolean isEmpty = true;
        for (int index = 0; index < inventory.getSizeInventory(); index++) {
          if (!inventory.getStackInSlot(index).isEmpty()) {
            isEmpty = false;
            break;
          }
        }
        if (!isEmpty) {
          activeSetup.put(e.getEntityPlayer(), e.getPos());
        } else {
          ChatHelper.sendMessage(e.getEntityPlayer(), "Empty");
        }
      }
    }
  }

  public void handleBuy(EntityPlayer player, TileEntitySign sign) {
    if (RankUtils.hasPermission(player, "economy.sign.buy_create")) {
      if (activeSetup.containsKey(player)) {
        BlockPos pos = activeSetup.get(player);
        IInventory inv = (IInventory) player.world.getTileEntity(pos);
        ItemStack stack = ItemStack.EMPTY;
        for (int index = 0; index < Objects.requireNonNull(inv).getSizeInventory();
            index++) {
          if (!inv.getStackInSlot(index).isEmpty()) {
            stack = inv.getStackInSlot(index);
          }
        }
        sign.signText[0].getStyle().setColor(TextFormatting.RED);
        sign.signText[1].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[2].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[3].getStyle().setColor(TextFormatting.GOLD);
        sign.getTileData().setString(ITEM_DATA, StackConverter.toString(stack));
        sign.getTileData()
            .setIntArray(CHEST_DATA, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        sign.getTileData()
            .setString(OWNER_DATA, player.getGameProfile().getId().toString());
        sign.markDirty();
      } else {

      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper
          .sendHoverMessage(player, noPerms,
              TextFormatting.RED + "economy.sign.buy_create");
    }
  }

  public void handleSell(EntityPlayer player, TileEntitySign sign) {
    if (RankUtils.hasPermission(player, "economy.sign.sell_create")) {
      if (activeSetup.containsKey(player)) {
        BlockPos pos = activeSetup.get(player);
        IInventory inv = (IInventory) player.world.getTileEntity(pos);
        ItemStack stack = ItemStack.EMPTY;
        for (int index = 0; index < Objects.requireNonNull(inv).getSizeInventory();
            index++) {
          if (!inv.getStackInSlot(index).isEmpty()) {
            stack = inv.getStackInSlot(index);
          }
        }
        sign.signText[0].getStyle().setColor(TextFormatting.RED);
        sign.signText[1].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[2].getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        sign.signText[3].getStyle().setColor(TextFormatting.GOLD);
        sign.getTileData().setString(ITEM_DATA, StackConverter.toString(stack));
        sign.getTileData()
            .setIntArray(CHEST_DATA, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        sign.getTileData()
            .setString(OWNER_DATA, player.getGameProfile().getId().toString());
        sign.markDirty();
      } else {

      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper
          .sendHoverMessage(player, noPerms,
              TextFormatting.RED + "economy.sign.sell_create");
    }
  }

}
