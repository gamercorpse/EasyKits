package com.gamercorpse.easykits.sessions;

import com.gamercorpse.easykits.models.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorSession {

    private static final Map<UUID, EditorSession> SESSIONS = new HashMap<>();

    private final UUID playerId;
    private final Kit kit;

    private boolean editingDisplayName;
    private boolean editingPermission;
    private boolean editingCooldown;
    private boolean editingSlot;
    private boolean editingCategory;
    private boolean editingCommand;

    private boolean editingItemDisplayName;
    private boolean editingItemLore;
    private boolean editingItemMaterial;
    private boolean editingItemAmount;
    private boolean editingItemCustomModelData;
    private boolean editingItemEnchantments;
    private boolean editingItemFlags;

    private String editingItemKey;

    public EditorSession(UUID playerId, Kit kit) {
        this.playerId = playerId;
        this.kit = kit;
    }

    public static void create(UUID uuid, Kit kit) {
        SESSIONS.put(uuid, new EditorSession(uuid, kit));
    }

    public static EditorSession get(UUID uuid) {
        return SESSIONS.get(uuid);
    }

    public static void remove(UUID uuid) {
        SESSIONS.remove(uuid);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean isWaitingForChat() {
        return editingDisplayName
                || editingPermission
                || editingCooldown
                || editingSlot
                || editingCategory
                || editingCommand
                || editingItemDisplayName
                || editingItemLore
                || editingItemMaterial
                || editingItemAmount
                || editingItemCustomModelData
                || editingItemEnchantments
                || editingItemFlags;
    }

    public void clearItemEditStates() {
        editingItemDisplayName = false;
        editingItemLore = false;
        editingItemMaterial = false;
        editingItemAmount = false;
        editingItemCustomModelData = false;
        editingItemEnchantments = false;
        editingItemFlags = false;
    }

    public boolean isEditingDisplayName() {
        return editingDisplayName;
    }

    public void setEditingDisplayName(boolean editingDisplayName) {
        this.editingDisplayName = editingDisplayName;
    }

    public boolean isEditingPermission() {
        return editingPermission;
    }

    public void setEditingPermission(boolean editingPermission) {
        this.editingPermission = editingPermission;
    }

    public boolean isEditingCooldown() {
        return editingCooldown;
    }

    public void setEditingCooldown(boolean editingCooldown) {
        this.editingCooldown = editingCooldown;
    }

    public boolean isEditingSlot() {
        return editingSlot;
    }

    public void setEditingSlot(boolean editingSlot) {
        this.editingSlot = editingSlot;
    }

    public boolean isEditingCategory() {
        return editingCategory;
    }

    public void setEditingCategory(boolean editingCategory) {
        this.editingCategory = editingCategory;
    }

    public boolean isEditingCommand() {
        return editingCommand;
    }

    public void setEditingCommand(boolean editingCommand) {
        this.editingCommand = editingCommand;
    }

    public boolean isEditingItemDisplayName() {
        return editingItemDisplayName;
    }

    public void setEditingItemDisplayName(boolean editingItemDisplayName) {
        this.editingItemDisplayName = editingItemDisplayName;
    }

    public boolean isEditingItemLore() {
        return editingItemLore;
    }

    public void setEditingItemLore(boolean editingItemLore) {
        this.editingItemLore = editingItemLore;
    }

    public boolean isEditingItemMaterial() {
        return editingItemMaterial;
    }

    public void setEditingItemMaterial(boolean editingItemMaterial) {
        this.editingItemMaterial = editingItemMaterial;
    }

    public boolean isEditingItemAmount() {
        return editingItemAmount;
    }

    public void setEditingItemAmount(boolean editingItemAmount) {
        this.editingItemAmount = editingItemAmount;
    }

    public boolean isEditingItemCustomModelData() {
        return editingItemCustomModelData;
    }

    public void setEditingItemCustomModelData(boolean editingItemCustomModelData) {
        this.editingItemCustomModelData = editingItemCustomModelData;
    }

    public boolean isEditingItemEnchantments() {
        return editingItemEnchantments;
    }

    public void setEditingItemEnchantments(boolean editingItemEnchantments) {
        this.editingItemEnchantments = editingItemEnchantments;
    }

    public boolean isEditingItemFlags() {
        return editingItemFlags;
    }

    public void setEditingItemFlags(boolean editingItemFlags) {
        this.editingItemFlags = editingItemFlags;
    }

    public String getEditingItemKey() {
        return editingItemKey;
    }

    public void setEditingItemKey(String editingItemKey) {
        this.editingItemKey = editingItemKey;
    }
}