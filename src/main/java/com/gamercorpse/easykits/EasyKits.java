package com.gamercorpse.easykits;

import com.gamercorpse.easykits.commands.CreateKitCommand;
import com.gamercorpse.easykits.commands.DeleteKitCommand;
import com.gamercorpse.easykits.commands.GiveKitCommand;
import com.gamercorpse.easykits.commands.KitCommand;
import com.gamercorpse.easykits.commands.ReloadCommand;
import com.gamercorpse.easykits.commands.tabcompleters.EditTabCompleter;
import com.gamercorpse.easykits.commands.tabcompleters.KitTabCompleter;
import com.gamercorpse.easykits.listeners.InventoryListener;
import com.gamercorpse.easykits.listeners.KitPreviewListener;
import com.gamercorpse.easykits.listeners.PlayerJoinListener;
import com.gamercorpse.easykits.managers.ConfigManager;
import com.gamercorpse.easykits.managers.CooldownManager;
import com.gamercorpse.easykits.managers.GUIManager;
import com.gamercorpse.easykits.managers.KitManager;
import com.gamercorpse.easykits.managers.MessageManager;
import com.gamercorpse.easykits.storage.YamlKitStorage;
import com.gamercorpse.easykits.utils.FoliaUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyKits extends JavaPlugin {

    private static EasyKits instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private KitManager kitManager;
    private CooldownManager cooldownManager;
    private GUIManager guiManager;
    private FoliaUtil foliaUtil;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.foliaUtil = new FoliaUtil(this);
        this.cooldownManager = new CooldownManager();
        this.guiManager = new GUIManager();
        this.kitManager = new KitManager(this, new YamlKitStorage(this));

        registerCommands();
        registerListeners();

        getLogger().info("EasyKits enabled successfully.");
    }

    @Override
    public void onDisable() {

        getLogger().info("EasyKits disabled.");
    }

    private void registerCommands() {

        KitCommand kitCommand = new KitCommand(this);

        getCommand("easykits").setExecutor(kitCommand);
        getCommand("easykits").setTabCompleter(new KitTabCompleter(this));

        new CreateKitCommand(this);
        new DeleteKitCommand(this);
        new GiveKitCommand(this);
        new ReloadCommand(this);

        new EditTabCompleter(this);
    }

    private void registerListeners() {

        getServer().getPluginManager().registerEvents(
                new PlayerJoinListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new InventoryListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new KitPreviewListener(this),
                this
        );
    }

    public static EasyKits getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public FoliaUtil getFoliaUtil() {
        return foliaUtil;
    }
}