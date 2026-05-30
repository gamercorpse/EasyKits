package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.KitEditorMenu;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.sessions.EditorSession;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditorListener implements Listener {

    private final EasyKits plugin;

    public EditorListener(EasyKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();

        if (!title.startsWith(KitEditorMenu.TITLE_PREFIX)) {
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0) {
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (rawSlot >= 54) {
            event.setCancelled(true);
            return;
        }

        EditorSession session = EditorSession.get(player.getUniqueId());

        if (session == null) {
            event.setCancelled(true);
            return;
        }

        Kit kit = session.getKit();

        if (rawSlot >= 32 && rawSlot <= 40) {
            return;
        }

        event.setCancelled(true);

        switch (rawSlot) {

            case 10 -> {
                session.setEditingDisplayName(true);
                player.closeInventory();
                player.sendMessage("Type the new display name in chat.");
            }

            case 12 -> {
                session.setEditingPermission(true);
                player.closeInventory();
                player.sendMessage("Type the new permission in chat. Type none to clear it.");
            }

            case 14 -> {
                session.setEditingCooldown(true);
                player.closeInventory();
                player.sendMessage("Type the cooldown in seconds.");
            }

            case 16 -> {
                kit.setOneTime(!kit.isOneTime());
                new KitEditorMenu(plugin).open(player, kit);
            }

            case 28 -> {
                ItemStack cursor = event.getCursor();

                if (cursor != null && cursor.getType() != Material.AIR) {
                    kit.setIconMaterial(cursor.getType().name());

                    if (cursor.hasItemMeta() && cursor.getItemMeta().hasCustomModelData()) {
                        kit.setIconModelData(cursor.getItemMeta().getCustomModelData());
                    } else {
                        kit.setIconModelData(0);
                    }

                    player.sendMessage("Kit icon updated.");
                    new KitEditorMenu(plugin).open(player, kit);
                } else {
                    player.sendMessage("Pick up an item on your cursor, then click the icon slot.");
                }
            }

            case 30 -> {
                session.setEditingSlot(true);
                player.closeInventory();
                player.sendMessage("Type the menu slot number.");
            }

            case 49 -> {
                Inventory inventory = event.getInventory();

                kit.setItems(KitEditorMenu.collectItems(inventory));

                plugin.getKitManager().save(kit);

                player.sendMessage("Kit saved.");
                player.closeInventory();
            }

            case 53 -> {
                plugin.getKitManager().delete(kit.getId());

                player.sendMessage("Kit deleted.");
                player.closeInventory();
            }

            default -> {
                // Protected editor GUI slot.
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        String title = event.getView().getTitle();

        if (!title.startsWith(KitEditorMenu.TITLE_PREFIX)) {
            return;
        }

        for (int slot : event.getRawSlots()) {

            if (slot >= 0 && slot < 54) {

                if (slot < 32 || slot > 40) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        EditorSession session = EditorSession.get(player.getUniqueId());

        if (session == null || !session.isWaitingForChat()) {
            return;
        }

        event.setCancelled(true);

        Kit kit = session.getKit();
        String message = event.getMessage();

        if (session.isEditingDisplayName()) {
            session.setEditingDisplayName(false);
            kit.setDisplayName(message);
            player.sendMessage("Display name updated.");
        } else if (session.isEditingPermission()) {
            session.setEditingPermission(false);

            if (message.equalsIgnoreCase("none") || message.equalsIgnoreCase("clear")) {
                kit.setPermission("");
            } else {
                kit.setPermission(message);
            }

            player.sendMessage("Permission updated.");
        } else if (session.isEditingCooldown()) {
            session.setEditingCooldown(false);

            try {
                kit.setCooldown(Long.parseLong(message));
                player.sendMessage("Cooldown updated.");
            } catch (NumberFormatException ex) {
                player.sendMessage("Invalid cooldown.");
            }
        } else if (session.isEditingSlot()) {
            session.setEditingSlot(false);

            try {
                kit.setSlot(Integer.parseInt(message));
                player.sendMessage("Slot updated.");
            } catch (NumberFormatException ex) {
                player.sendMessage("Invalid slot.");
            }
        }

        player.getScheduler().run(plugin, task ->
                        new KitEditorMenu(plugin).open(player, kit),
                null
        );
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        String title = event.getView().getTitle();

        if (!title.startsWith(KitEditorMenu.TITLE_PREFIX)) {
            return;
        }

        EditorSession session = EditorSession.get(event.getPlayer().getUniqueId());

        if (session != null && session.isWaitingForChat()) {
            return;
        }

        EditorSession.remove(event.getPlayer().getUniqueId());
    }
}