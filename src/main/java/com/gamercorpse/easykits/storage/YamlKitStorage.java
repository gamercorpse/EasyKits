package com.gamercorpse.easykits.storage;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YamlKitStorage implements KitStorage {

    private final EasyKits plugin;
    private final File kitsFolder;

    public YamlKitStorage(EasyKits plugin) {

        this.plugin = plugin;

        this.kitsFolder = new File(plugin.getDataFolder(), "kits");

        if (!kitsFolder.exists()) {
            kitsFolder.mkdirs();
        }

        ensureStarterKit();
    }

    private void ensureStarterKit() {

        File file = new File(kitsFolder, "starter.yml");

        if (file.exists()) return;

        YamlConfiguration cfg = new YamlConfiguration();

        cfg.set("id", "starter");
        cfg.set("display-name", "<gradient:#00ff99:#00ccff>Starter Kit</gradient>");
        cfg.set("permission", "easykits.kit.starter");
        cfg.set("cooldown", 3600);
        cfg.set("one-time", false);

        cfg.set("icon.material", "CHEST");
        cfg.set("icon.custom-model-data", 1001);

        cfg.set("slot", 10);

        cfg.set("commands", java.util.List.of("say %player% claimed starter"));

        cfg.set("items.sword.material", "DIAMOND_SWORD");
        cfg.set("items.sword.amount", 1);
        cfg.set("items.sword.custom-model-data", 55);
        cfg.set("items.sword.name", "<#00ffff>Starter Sword");
        cfg.set("items.sword.lore", java.util.List.of("<gray>A powerful starter weapon"));
        cfg.set("items.sword.enchantments.sharpness", 5);
        cfg.set("items.sword.unbreakable", true);

        cfg.set("items.food.material", "COOKED_BEEF");
        cfg.set("items.food.amount", 32);

        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Kit> loadKits() {

        Map<String, Kit> kits = new HashMap<>();

        File[] files = kitsFolder.listFiles();

        if (files == null) return kits;

        for (File file : files) {

            if (!file.getName().endsWith(".yml")) continue;

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            Kit kit = new Kit(cfg.getString("id"));

            kit.setDisplayName(cfg.getString("display-name"));
            kit.setPermission(cfg.getString("permission"));
            kit.setCooldown(cfg.getLong("cooldown"));
            kit.setOneTime(cfg.getBoolean("one-time"));

            kit.setIconMaterial(cfg.getString("icon.material"));
            kit.setIconModelData(cfg.getInt("icon.custom-model-data"));

            kit.setSlot(cfg.getInt("slot"));

            kits.put(kit.getId().toLowerCase(), kit);
        }

        return kits;
    }

    @Override
    public void saveKit(com.gamercorpse.easykits.models.Kit kit) {
        // implemented later (Stage 2 rewrite system)
    }

    @Override
    public void deleteKit(String id) {

        File file = new File(kitsFolder, id + ".yml");

        if (file.exists()) file.delete();
    }
}