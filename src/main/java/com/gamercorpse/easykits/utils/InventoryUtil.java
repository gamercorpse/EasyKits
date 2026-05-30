package com.gamercorpse.easykits.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    private InventoryUtil() {

    }

    public static void giveItems(Player player,
                                 Iterable<ItemStack> items) {

        for (ItemStack item : items) {

            if (item == null) {
                continue;
            }

            player.getInventory().addItem(
                    item.clone()
            );
        }
    }
}