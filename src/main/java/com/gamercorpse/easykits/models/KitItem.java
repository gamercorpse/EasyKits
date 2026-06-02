package com.gamercorpse.easykits.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitItem {

    private String material;
    private int amount = 1;

    private Integer customModelData;

    private String name;

    private List<String> lore;

    private Map<String, Integer> enchantments;

    private boolean unbreakable;

    private List<String> itemFlags;

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public void setItemFlags(List<String> itemFlags) {
        this.itemFlags = itemFlags;
    }

    public static KitItem fromItemStack(ItemStack item) {

        KitItem kitItem = new KitItem();

        if (item == null || item.getType() == Material.AIR) {
            kitItem.setMaterial("AIR");
            kitItem.setAmount(1);
            return kitItem;
        }

        kitItem.setMaterial(item.getType().name());
        kitItem.setAmount(item.getAmount());

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {

            MiniMessage mm = MiniMessage.miniMessage();

            if (meta.hasDisplayName()) {
                Component displayName = meta.displayName();

                if (displayName != null) {
                    kitItem.setName(mm.serialize(displayName));
                }
            }

            if (meta.hasLore() && meta.lore() != null) {
                List<String> serializedLore = meta.lore()
                        .stream()
                        .map(mm::serialize)
                        .collect(Collectors.toList());

                kitItem.setLore(serializedLore);
            }

            if (meta.hasCustomModelData()) {
                kitItem.setCustomModelData(meta.getCustomModelData());
            }

            kitItem.setUnbreakable(meta.isUnbreakable());

            if (!meta.getItemFlags().isEmpty()) {
                List<String> flags = new ArrayList<>();

                for (ItemFlag flag : meta.getItemFlags()) {
                    flags.add(flag.name());
                }

                kitItem.setItemFlags(flags);
            }
        }

        if (!item.getEnchantments().isEmpty()) {

            Map<String, Integer> enchantments = new HashMap<>();

            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                enchantments.put(entry.getKey().getKey().getKey(), entry.getValue());
            }

            kitItem.setEnchantments(enchantments);
        }

        return kitItem;
    }
}