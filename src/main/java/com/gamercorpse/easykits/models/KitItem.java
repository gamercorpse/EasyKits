package com.gamercorpse.easykits.models;

import org.bukkit.inventory.ItemStack;

public class KitItem {

    private final ItemStack itemStack;

    public KitItem(ItemStack itemStack) {

        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}