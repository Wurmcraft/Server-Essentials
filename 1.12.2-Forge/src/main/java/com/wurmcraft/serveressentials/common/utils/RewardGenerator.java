package com.wurmcraft.serveressentials.common.utils;

import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.models.Reward;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import joptsimple.internal.Strings;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RewardGenerator {

  public static final File REWARDS_FILE = new File(SAVE_DIR + File.separator + "rewards.json");
  public static HashMap<Integer, List<Reward>> rewards;
  private static final Random RAND = new Random(System.currentTimeMillis());

  public static void loadOrCreate() {
    if (REWARDS_FILE.exists()) {
      rewards = load();
      if (rewards != null) {
        return;
      }
    }
    rewards = createDefault();
    save();
  }

  private static HashMap<Integer, List<Reward>> load() {
    try {
      return ServerEssentials.GSON.fromJson(
          Strings.join(Files.readAllLines(REWARDS_FILE.toPath()), "\n"), HashMap.class);
    } catch (Exception e) {
      e.printStackTrace();
      ServerEssentials.LOG.warn("Failed to load rewards.json");
    }
    return null;
  }

  private static HashMap<Integer, List<Reward>> createDefault() {
    HashMap<Integer, List<Reward>> defaults = new HashMap<>();
    List<Reward> tier0 = new ArrayList<>();
    tier0.add(
        new Reward(
            ServerEssentials.stackConverter.toString(new ItemStack(Items.IRON_INGOT, 3)), 2));
    tier0.add(
        new Reward(
            ServerEssentials.stackConverter.toString(new ItemStack(Items.GOLD_INGOT, 2)), 1));
    defaults.put(0, tier0);
    List<Reward> tier1 = new ArrayList<>();
    tier1.add(
        new Reward(
            ServerEssentials.stackConverter.toString(new ItemStack(Items.DIAMOND, 2)), 1, 1));
    tier1.add(
        new Reward(
            ServerEssentials.stackConverter.toString(new ItemStack(Items.EMERALD, 2)), 1, 1));
    defaults.put(0, tier1);
    return defaults;
  }

  public static void save() {
    try {
      Files.write(
          REWARDS_FILE.toPath(),
          ServerEssentials.GSON.toJson(rewards).getBytes(),
          StandardOpenOption.WRITE,
          StandardOpenOption.CREATE);
    } catch (IOException e) {
      e.printStackTrace();
      ServerEssentials.LOG.warn("Failed to save updated rewards.json");
    }
  }

  public static ItemStack generateRandomReward(int tier) {
    if (rewards == null || rewards.isEmpty()) {
      loadOrCreate();
    }
    if (rewards.containsKey(tier)) {
      List<String> possibleItems = generateListOfItemsForTier(tier);
      return ServerEssentials.stackConverter.getData(
          possibleItems.get(RAND.nextInt(possibleItems.size())));
    }
    return ItemStack.EMPTY;
  }

  private static List<String> generateListOfItemsForTier(int tier) {
    List<String> possibilities = new ArrayList<>();
    for (Reward rewards : rewards.get(tier))
      for (int x = 0; x < rewards.chance; x++) possibilities.add(rewards.item);
    return possibilities;
  }

  public static List<String> getItemsForTier(int tier) {
    if (rewards == null) loadOrCreate();
    List<String> items = new ArrayList<>();
    for (Reward reward : rewards.get(tier)) items.add(reward.item);
    return items;
  }

  public static void addNewReward(Reward reward) {
    if (isValidReward(reward))
      if (rewards.containsKey(reward.tier)) {
        rewards.get(reward.tier).add(reward);
        save();
      } else {
        List<Reward> list = new ArrayList<>();
        list.add(reward);
        rewards.put(reward.tier, list);
        save();
      }
  }

  public static boolean isValidReward(Reward reward) {
    return reward != null && reward.chance > 0 && reward.tier >= 0;
  }

  public static boolean removeReward(ItemStack stack) {
    String conversion = ServerEssentials.stackConverter.toString(stack);
    Reward reward = null;
    for (int t : rewards.keySet()) {
      for (Reward r : rewards.get(t)) {
        if (r.item.equalsIgnoreCase(conversion)) {
          reward = r;
        }
      }
    }
    if (reward != null) {
      removeReward(reward);
      return true;
    }
    return false;
  }

  public static void removeReward(Reward reward) {
    rewards.get(reward.tier).remove(reward);
    save();
  }
}
