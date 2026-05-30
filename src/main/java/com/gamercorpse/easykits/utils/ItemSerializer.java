package com.gamercorpse.easykits.utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemSerializer {

    private ItemSerializer() {

    }

    public static List<ItemStack> cloneItems(List<ItemStack> items) {

        List<ItemStack> cloned = new ArrayList<>();

        for (ItemStack item : items) {

            if (item == null) {
                continue;
            }

            cloned.add(item.clone());
        }

        return cloned;
    }
}