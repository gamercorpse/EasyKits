package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.CommandEditorMenu;
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

import java.util.List;
import java.util.Map;

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

        if (title.startsWith(KitEditorMenu.TITLE_PREFIX)) {
            handleKitEditorClick(event, player);
            return;
        }

        if (title.startsWith(CommandEditorMenu.TITLE_PREFIX)) {
            handleCommandEditorClick(event, player, title);
        }
    }

    private void handleKitEditorClick(InventoryClickEvent event, Player player) {

        if (!player.hasPermission("easykits.kit.edit")) {
            event.setCancelled(true);
            player.closeInventory();
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0) {
            return;
        }

        EditorSession session = EditorSession.get(player.getUniqueId());

        if (session == null) {
            event.setCancelled(true);
            return;
        }

        Kit kit = session.getKit();

        if (rawSlot >= 54) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

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
                    player.sendMessage("Pick up an item from your inventory, then click the kit icon slot.");
                }
            }

            case 30 -> {
                session.setEditingSlot(true);
                player.closeInventory();
                player.sendMessage("Type the menu slot number.");
            }

            case 45 -> {
                kit.setItems(KitEditorMenu.collectPlayerInventory(player));
                player.sendMessage("Imported up to 9 items from your inventory. Click Save Kit to save changes.");
                new KitEditorMenu(plugin).open(player, kit);
            }

            case 46 -> new CommandEditorMenu(plugin).open(player, kit);

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
            }
        }
    }

    private void handleCommandEditorClick(InventoryClickEvent event, Player player, String title) {

        event.setCancelled(true);

        if (!player.hasPermission("easykits.kit.edit")) {
            player.closeInventory();
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= 54) {
            return;
        }

        String kitId = title.substring(CommandEditorMenu.TITLE_PREFIX.length());

        Kit kit = plugin.getKitManager().getKit(kitId);

        if (kit == null) {
            player.closeInventory();
            return;
        }

        EditorSession session = EditorSession.get(player.getUniqueId());

        if (session == null) {
            EditorSession.create(player.getUniqueId(), kit);
            session = EditorSession.get(player.getUniqueId());
        }

        if (rawSlot == 49) {
            new KitEditorMenu(plugin).open(player, kit);
            return;
        }

        if (rawSlot == 53) {
            session.setEditingCommand(true);
            player.closeInventory();
            player.sendMessage("Type the command in chat. Do not include the leading slash. Use %player% for the player name.");
            return;
        }

        if (rawSlot >= 0 && rawSlot < 45) {

            List<Map.Entry<String, String>> commands = CommandEditorMenu.getSortedCommands(kit);

            if (rawSlot >= commands.size()) {
                return;
            }

            Map.Entry<String, String> entry = commands.get(rawSlot);

            kit.getCommands().remove(entry.getKey());

            player.sendMessage("Removed command: " + entry.getValue());
            new CommandEditorMenu(plugin).open(player, kit);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        String title = event.getView().getTitle();

        if (title.startsWith(CommandEditorMenu.TITLE_PREFIX)) {
            event.setCancelled(true);
            return;
        }

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

        if (!player.hasPermission("easykits.kit.edit")) {
            EditorSession.remove(player.getUniqueId());
            player.sendMessage("You do not have permission.");
            return;
        }

        Kit kit = session.getKit();
        String message = event.getMessage();

        if (session.isEditingDisplayName()) {
            session.setEditingDisplayName(false);
            kit.setDisplayName(message);
            player.sendMessage("Display name updated.");

            player.getScheduler().run(plugin, task ->
                            new KitEditorMenu(plugin).open(player, kit),
                    null
            );
            return;
        }

        if (session.isEditingPermission()) {
            session.setEditingPermission(false);

            if (message.equalsIgnoreCase("none") || message.equalsIgnoreCase("clear")) {
                kit.setPermission("");
            } else {
                kit.setPermission(message);
            }

            player.sendMessage("Permission updated.");

            player.getScheduler().run(plugin, task ->
                            new KitEditorMenu(plugin).open(player, kit),
                    null
            );
            return;
        }

        if (session.isEditingCooldown()) {
            session.setEditingCooldown(false);

            try {
                kit.setCooldown(Long.parseLong(message));
                player.sendMessage("Cooldown updated.");
            } catch (NumberFormatException ex) {
                player.sendMessage("Invalid cooldown.");
            }

            player.getScheduler().run(plugin, task ->
                            new KitEditorMenu(plugin).open(player, kit),
                    null
            );
            return;
        }

        if (session.isEditingSlot()) {
            session.setEditingSlot(false);

            try {
                kit.setSlot(Integer.parseInt(message));
                player.sendMessage("Slot updated.");
            } catch (NumberFormatException ex) {
                player.sendMessage("Invalid slot.");
            }

            player.getScheduler().run(plugin, task ->
                            new KitEditorMenu(plugin).open(player, kit),
                    null
            );
            return;
        }

        if (session.isEditingCommand()) {
            session.setEditingCommand(false);

            if (message.startsWith("/")) {
                message = message.substring(1);
            }

            if (!message.isBlank()) {
                kit.getCommands().put(CommandEditorMenu.nextCommandKey(kit), message);
                player.sendMessage("Command added.");
            } else {
                player.sendMessage("Command was empty.");
            }

            player.getScheduler().run(plugin, task ->
                            new CommandEditorMenu(plugin).open(player, kit),
                    null
            );
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        String title = event.getView().getTitle();

        if (!title.startsWith(KitEditorMenu.TITLE_PREFIX)
                && !title.startsWith(CommandEditorMenu.TITLE_PREFIX)) {
            return;
        }

        EditorSession session = EditorSession.get(event.getPlayer().getUniqueId());

        if (session != null && session.isWaitingForChat()) {
            return;
        }

        EditorSession.remove(event.getPlayer().getUniqueId());
    }
}