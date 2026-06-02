package com.gamercorpse.easykits.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static ItemStack build(com.gamercorpse.easykits.models.KitItem kitItem) {

        Material material;

        try {
            material = Material.valueOf(kitItem.getMaterial().toUpperCase());
        } catch (Exception e) {
            return new ItemStack(Material.BARRIER);
        }

        ItemStack item = new ItemStack(material, Math.max(1, kitItem.getAmount()));

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (kitItem.getName() != null) {
            meta.displayName(mm.deserialize(kitItem.getName()));
        }

        if (kitItem.getLore() != null) {
            List<Component> lore = kitItem.getLore()
                    .stream()
                    .map(mm::deserialize)
                    .toList();

            meta.lore(lore);
        }

        if (kitItem.isUnbreakable()) {
            meta.setUnbreakable(true);
        }

        if (kitItem.getCustomModelData() != null) {
            meta.setCustomModelData(kitItem.getCustomModelData());
        }

        if (kitItem.getEnchantments() != null) {

            for (Map.Entry<String, Integer> entry : kitItem.getEnchantments().entrySet()) {

                Enchantment ench = Enchantment.getByKey(
                        org.bukkit.NamespacedKey.minecraft(entry.getKey().toLowerCase())
                );

                if (ench != null) {
                    meta.addEnchant(ench, entry.getValue(), true);
                }
            }
        }

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