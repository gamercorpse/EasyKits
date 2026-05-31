package com.gamercorpse.easykits;

import com.gamercorpse.easykits.commands.KitCommand;
import com.gamercorpse.easykits.listeners.EditorListener;
import com.gamercorpse.easykits.listeners.KitMenuListener;
import com.gamercorpse.easykits.managers.CooldownManager;
import com.gamercorpse.easykits.managers.KitManager;
import com.gamercorpse.easykits.storage.YamlKitStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyKits extends JavaPlugin {

    private KitManager kitManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        YamlKitStorage storage = new YamlKitStorage(this);

        kitManager = new KitManager(storage);
        cooldownManager = new CooldownManager();

        kitManager.load();

        KitCommand command = new KitCommand(this);

        if (getCommand("kits") != null) {
            getCommand("kits").setExecutor(command);
            getCommand("kits").setTabCompleter(command);
        }

        if (getCommand("easykits") != null) {
            getCommand("easykits").setExecutor(command);
            getCommand("easykits").setTabCompleter(command);
        }

        getServer().getPluginManager().registerEvents(
                new KitMenuListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new EditorListener(this),
                this
        );

        getLogger().info("EasyKits enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("EasyKits disabled.");
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}