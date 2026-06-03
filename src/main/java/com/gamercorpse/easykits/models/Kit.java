package com.gamercorpse.easykits.models;

import java.util.HashMap;
import java.util.Map;

public class Kit {

    private final String id;

    private String displayName;
    private String permission;
    private long cooldown;

    private boolean oneTime;

    private String iconMaterial;
    private int iconModelData;
    private String serializedIcon;

    private int slot;

    private String category = "default";

    private Map<String, KitItem> items = new HashMap<>();

    private Map<String, String> commands = new HashMap<>();

    private KitItem helmet;
    private KitItem chestplate;
    private KitItem leggings;
    private KitItem boots;
    private KitItem offhand;

    public Kit(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public String getIconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(String iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public int getIconModelData() {
        return iconModelData;
    }

    public void setIconModelData(int iconModelData) {
        this.iconModelData = iconModelData;
    }

    public String getSerializedIcon() {
        return serializedIcon;
    }

    public void setSerializedIcon(String serializedIcon) {
        this.serializedIcon = serializedIcon;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            this.category = "default";
        } else {
            this.category = category.toLowerCase();
        }
    }

    public Map<String, KitItem> getItems() {
        return items;
    }

    public void setItems(Map<String, KitItem> items) {
        this.items = items;
    }

    public Map<String, String> getCommands() {
        return commands;
    }

    public void setCommands(Map<String, String> commands) {
        this.commands = commands;
    }

    public KitItem getHelmet() {
        return helmet;
    }

    public void setHelmet(KitItem helmet) {
        this.helmet = helmet;
    }

    public KitItem getChestplate() {
        return chestplate;
    }

    public void setChestplate(KitItem chestplate) {
        this.chestplate = chestplate;
    }

    public KitItem getLeggings() {
        return leggings;
    }

    public void setLeggings(KitItem leggings) {
        this.leggings = leggings;
    }

    public KitItem getBoots() {
        return boots;
    }

    public void setBoots(KitItem boots) {
        this.boots = boots;
    }

    public KitItem getOffhand() {
        return offhand;
    }

    public void setOffhand(KitItem offhand) {
        this.offhand = offhand;
    }
}