package com.wurmcraft.serveressentials.forge.modules.protect.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData.ClaimType;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.time.Instant;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "Protect", name = "Rent")
public class RentCommand {

  @Command(inputArguments = {CommandArguments.STRING, CommandArguments.DOUBLE,
      CommandArguments.DOUBLE}, inputNames = {"setup", "days", "cost"})
  public void convertToRent(ICommandSender sender, String arg, double days, double cost) {
    if (arg.equalsIgnoreCase("setup")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        ClaimData claim = ProtectionHelper
            .getClaim(player.getPosition(), player.dimension);
        if (claim != null) {
          claim.claimType = ClaimType.RENT;
          claim.claimTypeData = "0 " + days + " " + cost + " " + (Instant.now().getEpochSecond() + (days * 86400));
          ProtectionHelper.saveClaimData(claim, player.dimension);
        }
      }
    }
  }

  @Command(inputArguments = {})
  public void rent(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (RankUtils.hasPermission(sender, "protect.rent.claim")) {
        ClaimData claim = ProtectionHelper
            .getClaim(player.getPosition(), player.dimension);
        if (claim != null && claim.claimType == ClaimType.RENT) {
          claim = ProtectionHelper.checkRent(claim);
          ProtectionHelper.saveClaimData(claim, player.dimension);
          double[] rentData = getRentData(claim);
          if (claim.trusted.length == 0) {
            if (EcoUtils.hasCurrency(player, rentData[2])) {
              claim.trusted = new String[]{player.getGameProfile().getId().toString()};
              claim = addMoneyToRent(claim, rentData[2]);
              ProtectionHelper.saveClaimData(claim, player.dimension);
              ChatHelper.sendMessage(player, "You have rented this claim for " + rentData[1] + " days");
            } else {
              ChatHelper.sendMessage(player, "You need at least " + rentData[2]);
            }
          } else {
            ChatHelper.sendMessage(player, "Claim is already rented");
          }
        } else {
          ChatHelper.sendMessage(player, "Not within a Rental Claim");
        }
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper
            .sendHoverMessage(sender, noPerms,
                TextFormatting.RED + "protect.rent.claim");
      }
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.DOUBLE}, inputNames = {"add", "amount"})
  public void addCurrency(ICommandSender sender, String arg, double amount) {
    if (arg.equalsIgnoreCase("add")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        ClaimData claim = ProtectionHelper
            .getClaim(player.getPosition(), player.dimension);
        if (claim != null && claim.claimType == ClaimType.RENT) {
          for (String uuid : claim.trusted) {
            if (player.getGameProfile().getId().toString().equals(uuid)) {
              if (EcoUtils.hasCurrency(player, amount)) {
                EcoUtils.consumeCurrency(player, amount);
                claim = addMoneyToRent(claim, amount);
                ProtectionHelper.saveClaimData(claim, player.dimension);
                ChatHelper
                    .sendMessage(player,
                        "You have added " + amount + " to the given rent");
              } else {
                ChatHelper.sendMessage(player, "You need at least " + amount);
              }
            }
          }
        }
      }
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.INTEGER}, inputNames = {"add", "amount"})
  public void addCurrency(ICommandSender sender, String arg, int amount) {
    addCurrency(sender, arg, (double) amount);
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"info"})
  public void displayInfo(ICommandSender sender, String arg) {
    if (arg.equalsIgnoreCase("info")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        ClaimData claim = ProtectionHelper
            .getClaim(player.getPosition(), player.dimension);
        if (claim != null && claim.claimType == ClaimType.RENT) {
          double[] rentInfo = getRentData(claim);
          ChatHelper.sendMessage(player, "Bank: " + rentInfo[0]);
          ChatHelper.sendMessage(player, "Rent Period (In Days): " + rentInfo[1]);
          ChatHelper.sendMessage(player, "Cost Per Period: " + rentInfo[2]);
          ChatHelper.sendMessage(player,
              "Days Left: " + (rentInfo[0] / (rentInfo[1] * rentInfo[2])));
          ChatHelper.sendMessage(player, "Next Payment: " + String.format("%.2f", ((rentInfo[3]- Instant.now().getEpochSecond()) / 3600)) + "h");
        }
      }
    }
  }

  public static double[] getRentData(ClaimData data) {
    String[] rentData = data.claimTypeData.split(" ");
    try {
      return new double[]{Double.parseDouble(rentData[0]),
          Double.parseDouble(rentData[1]), Double.parseDouble(rentData[2]), Double.parseDouble(rentData[3])};
    } catch (NumberFormatException ignored) {
    }
    return new double[]{0, 0, 0};
  }

  public static ClaimData addMoneyToRent(ClaimData claim, double amount) {
    double[] claimData = getRentData(claim);
    claimData[0] += amount;
    claim.claimTypeData = claimData[0] + " " + claimData[1] + " " + claimData[2] + " " + claimData[3];
    return claim;
  }

  public static ClaimData handleRentDay(ClaimData claim, double amount) {
    double[] claimData = getRentData(claim);
    claimData[0] -= amount;
    claim.claimTypeData = claimData[0] + " " + claimData[1] + " " + claimData[2] + " " + (Instant.now().getEpochSecond() + (claimData[1] * 86400));
    return claim;
  }
}
