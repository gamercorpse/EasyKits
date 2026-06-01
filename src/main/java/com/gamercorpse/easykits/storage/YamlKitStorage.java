package com.gamercorpse.easykits.storage;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import org.bukkit.configuration.ConfigurationSection;
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

        cfg.set("equipment.helmet.material", "LEATHER_HELMET");
        cfg.set("equipment.helmet.amount", 1);

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

            if (cfg.isList("commands")) {
                int i = 0;
                for (String cmd : cfg.getStringList("commands")) {
                    kit.getCommands().put("cmd_" + i, cmd);
                    i++;
                }
            }

            Map<String, KitItem> items = new HashMap<>();

            if (cfg.isConfigurationSection("items")) {

                ConfigurationSection section = cfg.getConfigurationSection("items");

                if (section != null) {
                    for (String key : section.getKeys(false)) {

                        ConfigurationSection itemSec = section.getConfigurationSection(key);

                        KitItem item = loadItem(itemSec);

                        if (item != null) {
                            items.put(key, item);
                        }
                    }
                }
            }

            kit.setItems(items);

            if (cfg.isConfigurationSection("equipment")) {
                kit.setHelmet(loadItem(cfg.getConfigurationSection("equipment.helmet")));
                kit.setChestplate(loadItem(cfg.getConfigurationSection("equipment.chestplate")));
                kit.setLeggings(loadItem(cfg.getConfigurationSection("equipment.leggings")));
                kit.setBoots(loadItem(cfg.getConfigurationSection("equipment.boots")));
                kit.setOffhand(loadItem(cfg.getConfigurationSection("equipment.offhand")));
            }

            plugin.getLogger().info(
                    "[EasyKits] Loaded kit " + id + " with " + items.size() + " items"
            );

            kits.put(id.toLowerCase(), kit);
        }

        return kits;
    }

    @Override
    public void saveKit(Kit kit) {

        File file = new File(kitsFolder, kit.getId().toLowerCase() + ".yml");

        YamlConfiguration cfg = new YamlConfiguration();

        cfg.set("id", kit.getId());
        cfg.set("display-name", kit.getDisplayName());
        cfg.set("permission", kit.getPermission());
        cfg.set("cooldown", kit.getCooldown());
        cfg.set("one-time", kit.isOneTime());

        cfg.set("icon.material", kit.getIconMaterial());
        cfg.set("icon.custom-model-data", kit.getIconModelData());

        cfg.set("slot", kit.getSlot());

        if (kit.getCommands() != null && !kit.getCommands().isEmpty()) {
            cfg.set("commands", kit.getCommands().values());
        }

        if (kit.getItems() != null && !kit.getItems().isEmpty()) {

            for (Map.Entry<String, KitItem> entry : kit.getItems().entrySet()) {
                saveItem(cfg, "items." + entry.getKey(), entry.getValue());
            }
        }

        saveItem(cfg, "equipment.helmet", kit.getHelmet());
        saveItem(cfg, "equipment.chestplate", kit.getChestplate());
        saveItem(cfg, "equipment.leggings", kit.getLeggings());
        saveItem(cfg, "equipment.boots", kit.getBoots());
        saveItem(cfg, "equipment.offhand", kit.getOffhand());

        try {
            cfg.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("[EasyKits] Failed to save kit " + kit.getId());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteKit(String id) {

        File file = new File(kitsFolder, id.toLowerCase() + ".yml");

        if (file.exists()) {
            file.delete();
        }
    }

    private KitItem loadItem(ConfigurationSection itemSec) {

        if (itemSec == null) {
            return null;
        }

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

            Map<String, Integer> enchantments = new HashMap<>();

            ConfigurationSection enchSec = itemSec.getConfigurationSection("enchantments");

            if (enchSec != null) {
                for (String enchKey : enchSec.getKeys(false)) {
                    enchantments.put(enchKey, enchSec.getInt(enchKey));
                }
            }

            item.setEnchantments(enchantments);
        }

        item.setUnbreakable(itemSec.getBoolean("unbreakable"));

        return item;
    }

    private void saveItem(YamlConfiguration cfg, String path, KitItem item) {

        if (item == null || item.getMaterial() == null || item.getMaterial().isBlank()) {
            return;
        }

        cfg.set(path + ".material", item.getMaterial());
        cfg.set(path + ".amount", item.getAmount());

        if (item.getCustomModelData() != null) {
            cfg.set(path + ".custom-model-data", item.getCustomModelData());
        }

        if (item.getName() != null) {
            cfg.set(path + ".name", item.getName());
        }

        if (item.getLore() != null) {
            cfg.set(path + ".lore", item.getLore());
        }

        if (item.getEnchantments() != null) {
            for (Map.Entry<String, Integer> ench : item.getEnchantments().entrySet()) {
                cfg.set(path + ".enchantments." + ench.getKey(), ench.getValue());
            }
        }

        cfg.set(path + ".unbreakable", item.isUnbreakable());
    }
}