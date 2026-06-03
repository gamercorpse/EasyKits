package com.gamercorpse.easykits.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
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

    private String serializedItem;

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

    public String getSerializedItem() {
        return serializedItem;
    }

    public void setSerializedItem(String serializedItem) {
        this.serializedItem = serializedItem;
    }

    public static KitItem fromItemStack(ItemStack item) {

        KitItem kitItem = new KitItem();

        if (item == null || item.getType() == Material.AIR) {
            kitItem.setMaterial("AIR");
            kitItem.setAmount(1);
            return kitItem;
        }

        ItemStack clone = item.clone();

        kitItem.setSerializedItem(serializeItem(clone));
        kitItem.setMaterial(clone.getType().name());
        kitItem.setAmount(clone.getAmount());

        ItemMeta meta = clone.getItemMeta();

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

        if (!clone.getEnchantments().isEmpty()) {

            Map<String, Integer> enchantments = new HashMap<>();

            for (Map.Entry<Enchantment, Integer> entry : clone.getEnchantments().entrySet()) {
                enchantments.put(entry.getKey().getKey().getKey(), entry.getValue());
            }

            kitItem.setEnchantments(enchantments);
        }

        return kitItem;
    }

    public static String serializeItem(ItemStack item) {

        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ItemStack deserializeItem(String data) {

        if (data == null || data.isBlank()) {
            return null;
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            Object object = dataInput.readObject();

            dataInput.close();

            if (object instanceof ItemStack item) {
                return item;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}