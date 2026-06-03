package com.gamercorpse.easykits.utils;

import com.gamercorpse.easykits.models.KitItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static ItemStack build(KitItem kitItem) {

        ItemStack item = KitItem.deserializeItem(kitItem.getSerializedItem());

        if (item == null) {

            Material material;

            try {
                material = Material.valueOf(kitItem.getMaterial().toUpperCase());
            } catch (Exception e) {
                return new ItemStack(Material.BARRIER);
            }

            item = new ItemStack(material, Math.max(1, kitItem.getAmount()));
        }

        if (kitItem.getMaterial() != null) {
            try {
                Material material = Material.valueOf(kitItem.getMaterial().toUpperCase());

                if (!material.isAir()) {
                    item.setType(material);
                }
            } catch (Exception ignored) {
            }
        }

        item.setAmount(Math.max(1, kitItem.getAmount()));

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        if (kitItem.getName() != null) {
            meta.displayName(mm.deserialize(kitItem.getName()));
        } else if (meta.hasDisplayName()) {
            meta.displayName(null);
        }

        if (kitItem.getLore() != null) {
            List<Component> lore = kitItem.getLore()
                    .stream()
                    .map(mm::deserialize)
                    .toList();

            meta.lore(lore);
        } else if (meta.hasLore()) {
            meta.lore(null);
        }

        meta.setUnbreakable(kitItem.isUnbreakable());

        if (kitItem.getCustomModelData() != null) {
            meta.setCustomModelData(kitItem.getCustomModelData());
        } else if (meta.hasCustomModelData()) {
            meta.setCustomModelData(null);
        }

        for (Enchantment enchantment : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchantment);
        }

        if (kitItem.getEnchantments() != null) {

            for (Map.Entry<String, Integer> entry : kitItem.getEnchantments().entrySet()) {

                Enchantment ench = Enchantment.getByKey(
                        NamespacedKey.minecraft(entry.getKey().toLowerCase())
                );

                if (ench != null) {
                    meta.addEnchant(ench, entry.getValue(), true);
                }
            }
        }

        meta.removeItemFlags(ItemFlag.values());

        if (kitItem.getItemFlags() != null) {
            for (String flagName : kitItem.getItemFlags()) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
                } catch (Exception ignored) {
                }
            }
        }

        item.setItemMeta(meta);

        return item;
    }
}