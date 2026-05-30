package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final EasyKits plugin;

    public PlayerJoinListener(EasyKits plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (plugin.getConfig().getBoolean("settings.debug")) {

            plugin.getLogger().info(
                    event.getPlayer().getName() + " joined the server."
            );
        }
    }
}