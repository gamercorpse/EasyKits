package com.gamercorpse.easykits.storage;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlKitStorage implements KitStorage {

    private final EasyKits plugin;
    private final File kitsFolder;

    public YamlKitStorage(EasyKits plugin) {

        this.plugin = plugin;

        this.kitsFolder = new File(
                plugin.getDataFolder(),
                "kits"
        );

        if (!kitsFolder.exists()) {
            kitsFolder.mkdirs();
        }
    }

    @Override
    public Map<String, Kit> loadKits() {

        Map<String, Kit> loadedKits = new HashMap<>();

        File[] files = kitsFolder.listFiles();

        if (files == null) {
            return loadedKits;
        }

        for (File file : files) {

            if (!file.getName().endsWith(".yml")) {
                continue;
            }

            YamlConfiguration configuration =
                    YamlConfiguration.loadConfiguration(file);

            String id = configuration.getString("id");

            if (id == null) {
                continue;
            }

            Kit kit = new Kit(id);

            kit.setDisplayName(
                    configuration.getString(
                            "display-name",
                            id
                    )
            );

            kit.setPermission(
                    configuration.getString(
                            "permission",
                            "easykits.kit." + id
                    )
            );

            kit.setCooldown(
                    configuration.getLong(
                            "cooldown",
                            0L
                    )
            );

            List<ItemStack> items = new ArrayList<>();

            List<?> rawItems = configuration.getList("items");

            if (rawItems != null) {

                for (Object object : rawItems) {

                    if (!(object instanceof ItemStack item)) {
                        continue;
                    }

                    items.add(item);
                }
            }

            kit.setItems(items);

            loadedKits.put(
                    id.toLowerCase(),
                    kit
            );
        }

        return loadedKits;
    }

    @Override
    public void saveKit(Kit kit) {

        File file = new File(
                kitsFolder,
                kit.getId() + ".yml"
        );

        YamlConfiguration configuration =
                new YamlConfiguration();

        configuration.set("id", kit.getId());
        configuration.set("display-name", kit.getDisplayName());
        configuration.set("permission", kit.getPermission());
        configuration.set("cooldown", kit.getCooldown());
        configuration.set("items", kit.getItems());

        try {

            configuration.save(file);

        } catch (IOException exception) {

            exception.printStackTrace();
        }
    }

    @Override
    public void deleteKit(String id) {

        File file = new File(
                kitsFolder,
                id + ".yml"
        );

        if (!file.exists()) {
            return;
        }

        file.delete();
    }
}