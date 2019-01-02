package org.bukkit.craftbukkit.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public final class CraftMagicNumbers{

    private CraftMagicNumbers() {}

    public static Block getBlock(org.bukkit.block.Block block) {
        return getBlock(block.getType());
    }

    @Deprecated
    // A bad method for bad magic.
    public static Block getBlock(int id) {
        return getBlock(Material.getBlockMaterial(id));
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(Block block) {
        return Block.getIdFromBlock(block);
    }

    public static Material getMaterial(Block block) {
        return Material.getBlockMaterial(Block.getIdFromBlock(block));
    }

    public static Item getItem(Material material) {
        // TODO: Don't use ID
        return Item.getItemById(material.getId());
    }

    @Deprecated
    // A bad method for bad magic.
    public static Item getItem(int id) {
        return Item.getItemById(id);
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(Item item) {
        return Item.getIdFromItem(item);
    }

    public static Material getMaterial(Item item) {
        // TODO: Don't use ID
        Material material = Material.getMaterial(Item.getIdFromItem(item));

        if (material == null) {
            return Material.AIR;
        }

        return material;
    }

    public static Block getBlock(Material material) {
        material = material == null ? Material.AIR : material; // PFServer - this should not happen but just in case it does
        // TODO: Don't use ID
        Block block = Block.getBlockById(material.getId());

        if (block == null) {
            return Blocks.AIR;
        }

        return block;
    }

    public Material getMaterialFromInternalName(String name) {
        return getMaterial((Item) Item.REGISTRY.getObject(new ResourceLocation(name)));
    }

    public List<String> tabCompleteInternalMaterialName(String token, List<String> completions) {
        ArrayList<String> results = Lists.newArrayList();
        for (ResourceLocation key : Item.REGISTRY.getKeys()) {
            results.add(key.toString());
        }
        return StringUtil.copyPartialMatches(token, results, completions);
    }

    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        net.minecraft.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        try {
            nmsStack.setTagCompound((NBTTagCompound) JsonToNBT.getTagFromJson(arguments));
        } catch (NBTException ex) {
            Logger.getLogger(CraftMagicNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }

        stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));

        return stack;
    }

    public Statistic getStatisticFromInternalName(String name) {
        return CraftStatistic.getBukkitStatisticByName(name);
    }

    public List<String> tabCompleteInternalStatisticOrAchievementName(String token, List<String> completions) {
        List<String> matches = new ArrayList<>();
        for (StatBase statBase : StatList.ALL_STATS) {
            String statistic = (statBase).statId;
            if (statistic.startsWith(token)) {
                matches.add(statistic);
            }
        }
        return matches;
    }

    public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        if (Bukkit.getAdvancement(key) != null) {
            throw new IllegalArgumentException("Advancement " + key + " already exists.");
        }

        net.minecraft.advancements.Advancement.Builder nms = JsonUtils.gsonDeserialize(AdvancementManager.GSON, advancement, net.minecraft.advancements.Advancement.Builder.class);
        if (nms != null) {
            AdvancementManager.ADVANCEMENT_LIST.loadAdvancements(Maps.newHashMap(Collections.singletonMap(CraftNamespacedKey.toMinecraft(key), nms)));
            Advancement bukkit = Bukkit.getAdvancement(key);

            if (bukkit != null) {
                File file = new File(MinecraftServer.getServerInst().getAdvancementManager().advancementsDir, key.getNamespace() + File.separator + key.getKey() + ".json");
                file.getParentFile().mkdirs();

                try {
                    Files.write(advancement, file, Charsets.UTF_8);
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error saving advancement " + key, ex);
                }

                MinecraftServer.getServerInst().getPlayerList().reloadResources();

                return bukkit;
            }
        }

        return null;
    }

    public boolean removeAdvancement(NamespacedKey key) {
        File file = new File(MinecraftServer.getServerInst().getAdvancementManager().advancementsDir, key.getNamespace() + File.separator + key.getKey() + ".json");
        return file.delete();
    }

    /**
     * This helper class represents the different NBT Tags.
     * <p>
     * These should match NBTBase#getTypeId
     */
    public static class NBT {

        public static final int TAG_END = 0;
        public static final int TAG_BYTE = 1;
        public static final int TAG_SHORT = 2;
        public static final int TAG_INT = 3;
        public static final int TAG_LONG = 4;
        public static final int TAG_FLOAT = 5;
        public static final int TAG_DOUBLE = 6;
        public static final int TAG_BYTE_ARRAY = 7;
        public static final int TAG_STRING = 8;
        public static final int TAG_LIST = 9;
        public static final int TAG_COMPOUND = 10;
        public static final int TAG_INT_ARRAY = 11;
        public static final int TAG_ANY_NUMBER = 99;
    }
}
