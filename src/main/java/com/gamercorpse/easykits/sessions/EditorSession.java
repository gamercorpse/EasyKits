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
    private boolean editingCommand;

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
        return editingDisplayName || editingPermission || editingCooldown || editingSlot || editingCommand;
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

    public boolean isEditingCommand() {
        return editingCommand;
    }

    public void setEditingCommand(boolean editingCommand) {
        this.editingCommand = editingCommand;
    }
}