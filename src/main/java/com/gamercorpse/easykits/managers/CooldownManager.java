package com.gamercorpse.easykits.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Map<String, Long>> cooldowns;

    public CooldownManager() {

        this.cooldowns = new HashMap<>();
    }

    public void setCooldown(UUID uuid,
                            String kit,
                            long time) {

        cooldowns.computeIfAbsent(uuid,
                        value -> new HashMap<>())
                .put(kit.toLowerCase(), time);
    }

    public long getCooldown(UUID uuid,
                            String kit) {

        return cooldowns
                .getOrDefault(uuid, new HashMap<>())
                .getOrDefault(kit.toLowerCase(), 0L);
    }

    public boolean hasCooldown(UUID uuid,
                               String kit) {

        return System.currentTimeMillis()
                < getCooldown(uuid, kit);
    }
}