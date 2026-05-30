package com.gamercorpse.easykits.storage;

import com.gamercorpse.easykits.models.Kit;

import java.util.Map;

public interface KitStorage {

    Map<String, Kit> loadKits();

    void saveKit(Kit kit);

    void deleteKit(String id);
}