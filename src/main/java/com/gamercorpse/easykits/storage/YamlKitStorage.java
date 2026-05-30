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

            String id = cfg.getString("id");

            if (id == null) {
                plugin.getLogger().warning("[EasyKits] Invalid kit file: " + file.getName());
                continue;
            }

            Kit kit = new Kit(id);

            kit.setDisplayName(cfg.getString("display-name"));
            kit.setPermission(cfg.getString("permission"));
            kit.setCooldown(cfg.getLong("cooldown"));
            kit.setOneTime(cfg.getBoolean("one-time"));

            kit.setIconMaterial(cfg.getString("icon.material"));
            kit.setIconModelData(cfg.getInt("icon.custom-model-data"));
            kit.setSlot(cfg.getInt("slot"));

            // =========================
            // COMMANDS
            // =========================
            if (cfg.isList("commands")) {
                int i = 0;
                for (String cmd : cfg.getStringList("commands")) {
                    kit.getCommands().put("cmd_" + i, cmd);
                    i++;
                }
            }

            // =========================
            // ITEMS (CRITICAL FIX)
            // =========================
            Map<String, KitItem> items = new HashMap<>();

            if (cfg.isConfigurationSection("items")) {

                var section = cfg.getConfigurationSection("items");

                for (String key : section.getKeys(false)) {

                    var itemSec = section.getConfigurationSection(key);

                    if (itemSec == null) continue;

                    KitItem item = new KitItem();

                    item.setMaterial(itemSec.getString("material"));
                    item.setAmount(itemSec.getInt("amount", 1));

                    if (itemSec.contains("custom-model-data")) {
                        item.setCustomModelData(itemSec.getInt("custom-model-data"));
                    }

                    item.setName(itemSec.getString("name"));

                    if (itemSec.isList("lore")) {
                        item.setLore(itemSec.getStringList("lore"));
                    }

                    if (itemSec.isConfigurationSection("enchantments")) {

                        Map<String, Integer> ench = new HashMap<>();

                        var enchSec = itemSec.getConfigurationSection("enchantments");

                        for (String enchKey : enchSec.getKeys(false)) {
                            ench.put(enchKey, enchSec.getInt(enchKey));
                        }

                        item.setEnchantments(ench);
                    }

                    item.setUnbreakable(itemSec.getBoolean("unbreakable"));

                    items.put(key, item);
                }
            }

            kit.setItems(items);

            plugin.getLogger().info(
                    "[EasyKits] Loaded kit " + id + " with " + items.size() + " items"
            );

            kits.put(id.toLowerCase(), kit);
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