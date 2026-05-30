package com.gamercorpse.easykits.managers;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.storage.KitStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final EasyKits plugin;
    private final KitStorage storage;

    private final Map<String, Kit> kits;

    public KitManager(EasyKits plugin,
                      KitStorage storage) {

        this.plugin = plugin;
        this.storage = storage;

        this.kits = new HashMap<>();

        loadKits();
    }

    public void loadKits() {

        kits.clear();

        kits.putAll(storage.loadKits());
    }

    public void saveKit(Kit kit) {

        kits.put(kit.getId().toLowerCase(), kit);

        storage.saveKit(kit);
    }

    public void deleteKit(String id) {

        kits.remove(id.toLowerCase());

        storage.deleteKit(id);
    }

    public boolean exists(String id) {

        return kits.containsKey(id.toLowerCase());
    }

    public Kit getKit(String id) {

        return kits.get(id.toLowerCase());
    }

    public Collection<Kit> getKits() {

        return kits.values();
    }
}