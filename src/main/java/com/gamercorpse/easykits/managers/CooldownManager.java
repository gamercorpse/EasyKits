package com.gamercorpse.easykits.managers;

import com.gamercorpse.easykits.models.Kit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(Player player, Kit kit) {

        if (player.hasPermission("easykits.cooldown.bypass")) {
            return false;
        }

        long cooldownSeconds = kit.getCooldown();

        if (cooldownSeconds <= 0) {
            return false;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());

        if (playerCooldowns == null) {
            return false;
        }

        Long lastClaim = playerCooldowns.get(kit.getId().toLowerCase());

        if (lastClaim == null) {
            return false;
        }

        long elapsedMillis = System.currentTimeMillis() - lastClaim;
        long cooldownMillis = cooldownSeconds * 1000L;

        return elapsedMillis < cooldownMillis;
    }

    public long getRemainingSeconds(Player player, Kit kit) {

        if (player.hasPermission("easykits.cooldown.bypass")) {
            return 0;
        }

        long cooldownSeconds = kit.getCooldown();

        if (cooldownSeconds <= 0) {
            return 0;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());

        if (playerCooldowns == null) {
            return 0;
        }

        Long lastClaim = playerCooldowns.get(kit.getId().toLowerCase());

        if (lastClaim == null) {
            return 0;
        }

        long elapsedMillis = System.currentTimeMillis() - lastClaim;
        long remainingMillis = cooldownSeconds * 1000L - elapsedMillis;

        if (remainingMillis <= 0) {
            return 0;
        }

        return (remainingMillis + 999L) / 1000L;
    }

    public void startCooldown(Player player, Kit kit) {

        if (kit.getCooldown() <= 0) {
            return;
        }

        cooldowns
                .computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                .put(kit.getId().toLowerCase(), System.currentTimeMillis());
    }

    public String formatTime(long seconds) {

        if (seconds <= 0) {
            return "0s";
        }

        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append("d ");
        }

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        if (minutes > 0) {
            builder.append(minutes).append("m ");
        }

        if (seconds > 0 || builder.isEmpty()) {
            builder.append(seconds).append("s");
        }

        return builder.toString().trim();
    }

    public void clearCooldown(Player player, Kit kit) {

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());

        if (playerCooldowns == null) {
            return;
        }

        playerCooldowns.remove(kit.getId().toLowerCase());

        if (playerCooldowns.isEmpty()) {
            cooldowns.remove(player.getUniqueId());
        }
    }

    public void clearAll(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}