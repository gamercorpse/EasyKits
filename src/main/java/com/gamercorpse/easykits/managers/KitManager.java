package com.gamercorpse.easykits.managers;

import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.storage.KitStorage;

import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final KitStorage storage;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(KitStorage storage) {
        this.storage = storage;
    }

    public void load() {
        kits.clear();
        kits.putAll(storage.loadKits());
    }

    public void save(Kit kit) {

        String key = kit.getId().toLowerCase();

        kits.put(key, kit);
        storage.saveKit(kit);
    }

    public Kit getKit(String id) {
        return kits.get(id.toLowerCase());
    }

    public boolean exists(String id) {
        return kits.containsKey(id.toLowerCase());
    }

    public Map<String, Kit> getKits() {
        return kits;
    }


    public void delete(String id) {

        String key = id.toLowerCase();

        kits.remove(key);
        storage.deleteKit(key);
    }
}