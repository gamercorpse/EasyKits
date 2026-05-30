package com.gamercorpse.easykits.managers;

import com.gamercorpse.easykits.EasyKits;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final EasyKits plugin;

    public ConfigManager(EasyKits plugin) {

        this.plugin = plugin;
    }

    public void reload() {

        plugin.reloadConfig();
    }

    public FileConfiguration getConfig() {

        return plugin.getConfig();
    }
}