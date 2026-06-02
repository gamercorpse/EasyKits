package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.CommandEditorMenu;
import com.gamercorpse.easykits.gui.ItemEditorMenu;
import com.gamercorpse.easykits.gui.ItemListEditorMenu;
import com.gamercorpse.easykits.gui.KitEditorMenu;
import com.gamercorpse.easykits.gui.PreviewMenu;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.sessions.EditorSession;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
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
            return;
        }

        if (title.startsWith(ItemListEditorMenu.TITLE_PREFIX)) {
            handleItemListClick(event, player, title);
            return;
        }

        if (title.startsWith(ItemEditorMenu.TITLE_PREFIX)) {
            handleItemEditorClick(event, player, title);
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
                player.sendMessage("Type the menu slot number. Slots 0-44 are page 1, 45-89 are page 2, etc.");
            }

            case 32 -> new PreviewMenu(plugin).open(player, kit);

            case 34 -> {
                KitEditorMenu.importPlayerInventory(player, kit);
                player.sendMessage("Imported inventory, armor, and offhand. Click Save Kit to save changes.");
                new KitEditorMenu(plugin).open(player, kit);
            }

            case 36 -> {
                session.setEditingCategory(true);
                player.closeInventory();
                player.sendMessage("Type the category name in chat. Type default to use the default category.");
            }

            case 38 -> new ItemListEditorMenu(plugin).open(player, kit);

            case 46 -> new CommandEditorMenu(plugin).open(player, kit);

            case 49 -> {
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

    private void handleItemListClick(InventoryClickEvent event, Player player, String title) {

        event.setCancelled(true);

        if (!player.hasPermission("easykits.kit.edit")) {
            player.closeInventory();
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= 54) {
            return;
        }

        String kitId = title.substring(ItemListEditorMenu.TITLE_PREFIX.length());
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
            ItemStack cursor = event.getCursor();

            if (cursor == null || cursor.getType() == Material.AIR) {
                player.sendMessage("Pick up an item on your cursor first.");
                return;
            }

            String key = ItemListEditorMenu.nextItemKey(kit);
            kit.getItems().put(key, KitItem.fromItemStack(cursor.clone()));
            player.sendMessage("Added item.");
            new ItemListEditorMenu(plugin).open(player, kit);
            return;
        }

        String itemKey = null;

        if (rawSlot >= 0 && rawSlot < 36) {
            itemKey = ItemListEditorMenu.getItemKeyBySlot(kit, rawSlot);
        } else if (rawSlot == 45) {
            itemKey = "equipment:helmet";
        } else if (rawSlot == 46) {
            itemKey = "equipment:chestplate";
        } else if (rawSlot == 47) {
            itemKey = "equipment:leggings";
        } else if (rawSlot == 48) {
            itemKey = "equipment:boots";
        } else if (rawSlot == 50) {
            itemKey = "equipment:offhand";
        }

        if (itemKey == null) {
            return;
        }

        KitItem item = getKitItem(kit, itemKey);

        if (item == null) {
            item = new KitItem();
            item.setMaterial("STONE");
            item.setAmount(1);
            setKitItem(kit, itemKey, item);
        }

        session.setEditingItemKey(itemKey);
        new ItemEditorMenu(plugin).open(player, kit.getId(), itemKey, item);
    }

    private void handleItemEditorClick(InventoryClickEvent event, Player player, String title) {

        event.setCancelled(true);

        if (!player.hasPermission("easykits.kit.edit")) {
            player.closeInventory();
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= 54) {
            return;
        }

        ItemTitleParts parts = parseItemEditorTitle(title);

        if (parts == null) {
            player.closeInventory();
            return;
        }

        Kit kit = plugin.getKitManager().getKit(parts.kitId());

        if (kit == null) {
            player.closeInventory();
            return;
        }

        KitItem item = getKitItem(kit, parts.itemKey());

        if (item == null) {
            item = new KitItem();
            item.setMaterial("STONE");
            item.setAmount(1);
            setKitItem(kit, parts.itemKey(), item);
        }

        EditorSession session = EditorSession.get(player.getUniqueId());

        if (session == null) {
            EditorSession.create(player.getUniqueId(), kit);
            session = EditorSession.get(player.getUniqueId());
        }

        session.setEditingItemKey(parts.itemKey());

        switch (rawSlot) {

            case 10 -> {
                session.clearItemEditStates();
                session.setEditingItemDisplayName(true);
                player.closeInventory();
                player.sendMessage("Type the item display name. Type clear to remove it.");
            }

            case 11 -> {
                session.clearItemEditStates();
                session.setEditingItemLore(true);
                player.closeInventory();
                player.sendMessage("Type lore lines separated by |. Type clear to remove lore.");
            }

            case 12 -> {
                session.clearItemEditStates();
                session.setEditingItemMaterial(true);
                player.closeInventory();
                player.sendMessage("Type a material name, for example DIAMOND_SWORD.");
            }

            case 13 -> {
                session.clearItemEditStates();
                session.setEditingItemAmount(true);
                player.closeInventory();
                player.sendMessage("Type an amount from 1 to 64.");
            }

            case 14 -> {
                session.clearItemEditStates();
                session.setEditingItemCustomModelData(true);
                player.closeInventory();
                player.sendMessage("Type custom model data number. Type clear to remove it.");
            }

            case 15 -> {
                session.clearItemEditStates();
                session.setEditingItemEnchantments(true);
                player.closeInventory();
                player.sendMessage("Type enchantment commands: sharpness 5, remove sharpness, or clear.");
            }

            case 16 -> {
                item.setUnbreakable(!item.isUnbreakable());
                new ItemEditorMenu(plugin).open(player, kit.getId(), parts.itemKey(), item);
            }

            case 30 -> {
                session.clearItemEditStates();
                session.setEditingItemFlags(true);
                player.closeInventory();
                player.sendMessage("Type item flags separated by commas, or clear. Example: HIDE_ATTRIBUTES,HIDE_ENCHANTS,HIDE_UNBREAKABLE");
            }

            case 45 -> new ItemListEditorMenu(plugin).open(player, kit);

            case 49 -> {
                plugin.getKitManager().save(kit);
                player.sendMessage("Kit saved.");
                new ItemListEditorMenu(plugin).open(player, kit);
            }

            case 53 -> {
                removeKitItem(kit, parts.itemKey());
                player.sendMessage("Item deleted.");
                new ItemListEditorMenu(plugin).open(player, kit);
            }

            default -> {
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        String title = event.getView().getTitle();

        if (title.startsWith(CommandEditorMenu.TITLE_PREFIX)
                || title.startsWith(ItemListEditorMenu.TITLE_PREFIX)
                || title.startsWith(ItemEditorMenu.TITLE_PREFIX)) {
            event.setCancelled(true);
            return;
        }

        if (title.startsWith(KitEditorMenu.TITLE_PREFIX)) {

            for (int slot : event.getRawSlots()) {

                if (slot >= 0 && slot < 54) {
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
            reopenKitEditor(player, kit);
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
            reopenKitEditor(player, kit);
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

            reopenKitEditor(player, kit);
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

            reopenKitEditor(player, kit);
            return;
        }

        if (session.isEditingCategory()) {
            session.setEditingCategory(false);

            if (message.isBlank()) {
                kit.setCategory("default");
            } else {
                kit.setCategory(message);
            }

            player.sendMessage("Category updated.");
            reopenKitEditor(player, kit);
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
            return;
        }

        handleItemChat(player, session, kit, message);
    }

    private void handleItemChat(Player player, EditorSession session, Kit kit, String message) {

        String key = session.getEditingItemKey();

        if (key == null) {
            session.clearItemEditStates();
            reopenKitEditor(player, kit);
            return;
        }

        KitItem item = getKitItem(kit, key);

        if (item == null) {
            item = new KitItem();
            item.setMaterial("STONE");
            item.setAmount(1);
            setKitItem(kit, key, item);
        }

        if (session.isEditingItemDisplayName()) {
            session.setEditingItemDisplayName(false);

            if (message.equalsIgnoreCase("clear") || message.equalsIgnoreCase("none")) {
                item.setName(null);
            } else {
                item.setName(message);
            }

            player.sendMessage("Item display name updated.");
            reopenItemEditor(player, kit, key, item);
            return;
        }

        if (session.isEditingItemLore()) {
            session.setEditingItemLore(false);

            if (message.equalsIgnoreCase("clear") || message.equalsIgnoreCase("none")) {
                item.setLore(null);
            } else {
                item.setLore(Arrays.asList(message.split("\\|")));
            }

            player.sendMessage("Item lore updated.");
            reopenItemEditor(player, kit, key, item);
            return;
        }

        if (session.isEditingItemMaterial()) {
            session.setEditingItemMaterial(false);

            try {
                Material material = Material.valueOf(message.toUpperCase());

                if (material.isAir()) {
                    player.sendMessage("Invalid material.");
                } else {
                    item.setMaterial(material.name());
                    player.sendMessage("Item material updated.");
                }
            } catch (Exception ex) {
                player.sendMessage("Invalid material.");
            }

            reopenItemEditor(player, kit, key, item);
            return;
        }

        if (session.isEditingItemAmount()) {
            session.setEditingItemAmount(false);

            try {
                int amount = Integer.parseInt(message);
                amount = Math.max(1, Math.min(64, amount));
                item.setAmount(amount);
                player.sendMessage("Item amount updated.");
            } catch (NumberFormatException ex) {
                player.sendMessage("Invalid amount.");
            }

            reopenItemEditor(player, kit, key, item);
            return;
        }

        if (session.isEditingItemCustomModelData()) {
            session.setEditingItemCustomModelData(false);

            if (message.equalsIgnoreCase("clear") || message.equalsIgnoreCase("none")) {
                item.setCustomModelData(null);
                player.sendMessage("Custom model data cleared.");
            } else {
                try {
                    item.setCustomModelData(Integer.parseInt(message));
                    player.sendMessage("Custom model data updated.");
                } catch (NumberFormatException ex) {
                    player.sendMessage("Invalid custom model data.");
                }
            }

            reopenItemEditor(player, kit, key, item);
            return;
        }

        if (session.isEditingItemEnchantments()) {
            session.setEditingItemEnchantments(false);
            editEnchantments(player, item, message);
            reopenItemEditor(player, kit, key, item);
            return;
        }

        if (session.isEditingItemFlags()) {
            session.setEditingItemFlags(false);

            if (message.equalsIgnoreCase("clear") || message.equalsIgnoreCase("none")) {
                item.setItemFlags(null);
                player.sendMessage("Item flags cleared.");
            } else {
                item.setItemFlags(Arrays.stream(message.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(String::toUpperCase)
                        .toList());

                player.sendMessage("Item flags updated.");
            }

            reopenItemEditor(player, kit, key, item);
        }
    }

    private void editEnchantments(Player player, KitItem item, String message) {

        if (message.equalsIgnoreCase("clear") || message.equalsIgnoreCase("none")) {
            item.setEnchantments(null);
            player.sendMessage("Enchantments cleared.");
            return;
        }

        Map<String, Integer> enchantments = item.getEnchantments();

        if (enchantments == null) {
            enchantments = new HashMap<>();
            item.setEnchantments(enchantments);
        }

        String[] parts = message.trim().split("\\s+");

        if (parts.length == 2 && parts[0].equalsIgnoreCase("remove")) {

            enchantments.remove(parts[1].toLowerCase());
            player.sendMessage("Enchantment removed.");
            return;
        }

        if (parts.length != 2) {
            player.sendMessage("Invalid enchantment format.");
            return;
        }

        Enchantment enchantment = Enchantment.getByKey(
                org.bukkit.NamespacedKey.minecraft(parts[0].toLowerCase())
        );

        if (enchantment == null) {
            player.sendMessage("Unknown enchantment.");
            return;
        }

        try {
            int level = Integer.parseInt(parts[1]);
            enchantments.put(enchantment.getKey().getKey(), level);
            player.sendMessage("Enchantment updated.");
        } catch (NumberFormatException ex) {
            player.sendMessage("Invalid enchantment level.");
        }
    }

    private void reopenKitEditor(Player player, Kit kit) {
        player.getScheduler().run(plugin, task ->
                        new KitEditorMenu(plugin).open(player, kit),
                null
        );
    }

    private void reopenItemEditor(Player player, Kit kit, String key, KitItem item) {
        player.getScheduler().run(plugin, task ->
                        new ItemEditorMenu(plugin).open(player, kit.getId(), key, item),
                null
        );
    }

    private KitItem getKitItem(Kit kit, String key) {

        return switch (key) {
            case "equipment:helmet" -> kit.getHelmet();
            case "equipment:chestplate" -> kit.getChestplate();
            case "equipment:leggings" -> kit.getLeggings();
            case "equipment:boots" -> kit.getBoots();
            case "equipment:offhand" -> kit.getOffhand();
            default -> kit.getItems().get(key);
        };
    }

    private void setKitItem(Kit kit, String key, KitItem item) {

        switch (key) {
            case "equipment:helmet" -> kit.setHelmet(item);
            case "equipment:chestplate" -> kit.setChestplate(item);
            case "equipment:leggings" -> kit.setLeggings(item);
            case "equipment:boots" -> kit.setBoots(item);
            case "equipment:offhand" -> kit.setOffhand(item);
            default -> kit.getItems().put(key, item);
        }
    }

    private void removeKitItem(Kit kit, String key) {

        switch (key) {
            case "equipment:helmet" -> kit.setHelmet(null);
            case "equipment:chestplate" -> kit.setChestplate(null);
            case "equipment:leggings" -> kit.setLeggings(null);
            case "equipment:boots" -> kit.setBoots(null);
            case "equipment:offhand" -> kit.setOffhand(null);
            default -> kit.getItems().remove(key);
        }
    }

    private ItemTitleParts parseItemEditorTitle(String title) {

        String value = title.substring(ItemEditorMenu.TITLE_PREFIX.length());

        int split = value.indexOf(":");

        if (split <= 0 || split >= value.length() - 1) {
            return null;
        }

        String kitId = value.substring(0, split);
        String itemKey = value.substring(split + 1);

        return new ItemTitleParts(kitId, itemKey);
    }

    private record ItemTitleParts(String kitId, String itemKey) {
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        String title = event.getView().getTitle();

        if (!title.startsWith(KitEditorMenu.TITLE_PREFIX)
                && !title.startsWith(CommandEditorMenu.TITLE_PREFIX)
                && !title.startsWith(ItemListEditorMenu.TITLE_PREFIX)
                && !title.startsWith(ItemEditorMenu.TITLE_PREFIX)) {
            return;
        }

        EditorSession session = EditorSession.get(event.getPlayer().getUniqueId());

        if (session != null && session.isWaitingForChat()) {
            return;
        }

        EditorSession.remove(event.getPlayer().getUniqueId());
    }
}