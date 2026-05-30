package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    private final EasyKits plugin;

    public InventoryListener(EasyKits plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

    }
}