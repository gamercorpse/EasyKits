package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.KitMenu;
import com.gamercorpse.easykits.gui.PreviewMenu;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.ItemBuilder;
import com.gamercorpse.easykits.utils.MessageUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitMenuListener implements Listener {

    private final EasyKits plugin;

    public KitMenuListener(EasyKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        String title = PlainTextComponentSerializer.plainText()
                .serialize(event.getView().title());

        if (title.equalsIgnoreCase(KitMenu.CATEGORY_TITLE_TEXT)) {
            handleCategoryMenuClick(event);
            return;
        }

        if (title.startsWith(KitMenu.KIT_TITLE_PREFIX)) {
            handleKitMenuClick(event, title);
            return;
        }

        if (title.startsWith(PreviewMenu.TITLE_PREFIX)) {
            handlePreviewMenuClick(event, title);
        }
    }

    private void handleCategoryMenuClick(InventoryClickEvent event) {

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        Set<String> categorySet = new KitMenu(plugin).getVisibleCategories(player);
        List<String> categories = new ArrayList<>(categorySet);

        if (rawSlot >= categories.size()) {
            return;
        }

        new KitMenu(plugin).open(player, categories.get(rawSlot), 0);
    }

    private void handleKitMenuClick(InventoryClickEvent event, String title) {

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        ParsedKitMenuTitle parsed = parseKitMenuTitle(title);

        if (parsed == null) {
            return;
        }

        if (rawSlot == 45) {
            new KitMenu(plugin).openCategories(player);
            return;
        }

        if (rawSlot == 48) {
            new KitMenu(plugin).open(player, parsed.category(), parsed.page() - 1);
            return;
        }

        if (rawSlot == 50) {
            new KitMenu(plugin).open(player, parsed.category(), parsed.page() + 1);
            return;
        }

        if (rawSlot >= KitMenu.KITS_PER_PAGE) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        Kit clickedKit = new KitMenu(plugin).getKitByDisplaySlot(
                player,
                parsed.category(),
                parsed.page(),
                rawSlot
        );

        if (clickedKit == null) {
            return;
        }

        if (!canAccess(player, clickedKit)) {
            return;
        }

        if (event.getClick() == ClickType.RIGHT ||
                event.getClick() == ClickType.SHIFT_RIGHT) {

            new PreviewMenu(plugin).open(player, clickedKit);
            return;
        }

        claimKit(player, clickedKit);
    }

    private void handlePreviewMenuClick(InventoryClickEvent event, String title) {

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        String kitId = title.substring(PreviewMenu.TITLE_PREFIX.length());

        Kit kit = plugin.getKitManager().getKit(kitId);

        if (kit == null) {
            player.closeInventory();
            return;
        }

        if (rawSlot == 49) {
            new KitMenu(plugin).open(player);
            return;
        }

        if (rawSlot == 53) {
            claimKit(player, kit);
        }
    }

    private ParsedKitMenuTitle parseKitMenuTitle(String title) {

        if (!title.startsWith(KitMenu.KIT_TITLE_PREFIX)) {
            return null;
        }

        String rest = title.substring(KitMenu.KIT_TITLE_PREFIX.length());

        int marker = rest.lastIndexOf(" - Page ");

        if (marker < 0) {
            return new ParsedKitMenuTitle(rest.toLowerCase(), 0);
        }

        String category = rest.substring(0, marker).toLowerCase();
        String pageText = rest.substring(marker + " - Page ".length());

        try {
            int page = Integer.parseInt(pageText) - 1;

            if (page < 0) {
                page = 0;
            }

            return new ParsedKitMenuTitle(category, page);
        } catch (NumberFormatException ex) {
            return new ParsedKitMenuTitle(category, 0);
        }
    }

    private boolean canAccess(Player player, Kit kit) {

        if (kit.getPermission() != null &&
                !kit.getPermission().isEmpty() &&
                !player.hasPermission(kit.getPermission())) {

            MessageUtil.send(
                    player,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>You do not have permission for this kit."
            );

            return false;
        }

        return true;
    }

    private boolean canClaim(Player player, Kit kit) {

        if (!canAccess(player, kit)) {
            return false;
        }

        if (plugin.getCooldownManager().isOnCooldown(player, kit)) {

            long remaining = plugin.getCooldownManager().getRemainingSeconds(player, kit);

            MessageUtil.send(
                    player,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>You must wait <white>" +
                            plugin.getCooldownManager().formatTime(remaining) +
                            "</white> before claiming this kit again."
            );

            return false;
        }

        return true;
    }

    private void claimKit(Player player, Kit kit) {

        if (!canClaim(player, kit)) {
            return;
        }

        giveKit(player, kit);
        runCommands(player, kit);

        plugin.getCooldownManager().startCooldown(player, kit);
    }

    private void giveKit(Player player, Kit kit) {

        if (kit.getItems() != null) {

            for (KitItem kitItem : kit.getItems().values()) {

                ItemStack item = ItemBuilder.build(kitItem);

                if (item == null || item.getType().isAir()) {
                    continue;
                }

                player.getInventory().addItem(item);
            }
        }

        giveEquipment(player, kit);

        MessageUtil.send(
                player,
                plugin.getConfig().getString("messages.prefix", ""),
                "<green>You claimed kit <white>" +
                        kit.getId() +
                        "</white>."
        );

        player.closeInventory();
    }

    private void giveEquipment(Player player, Kit kit) {

        PlayerInventory inventory = player.getInventory();

        equipOrAdd(inventory, EquipmentSlotType.HELMET, kit.getHelmet());
        equipOrAdd(inventory, EquipmentSlotType.CHESTPLATE, kit.getChestplate());
        equipOrAdd(inventory, EquipmentSlotType.LEGGINGS, kit.getLeggings());
        equipOrAdd(inventory, EquipmentSlotType.BOOTS, kit.getBoots());
        equipOrAdd(inventory, EquipmentSlotType.OFFHAND, kit.getOffhand());
    }

    private void equipOrAdd(PlayerInventory inventory, EquipmentSlotType slotType, KitItem kitItem) {

        if (kitItem == null) {
            return;
        }

        ItemStack item = ItemBuilder.build(kitItem);

        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemStack existing = switch (slotType) {
            case HELMET -> inventory.getHelmet();
            case CHESTPLATE -> inventory.getChestplate();
            case LEGGINGS -> inventory.getLeggings();
            case BOOTS -> inventory.getBoots();
            case OFFHAND -> inventory.getItemInOffHand();
        };

        if (existing == null || existing.getType() == Material.AIR) {

            switch (slotType) {
                case HELMET -> inventory.setHelmet(item);
                case CHESTPLATE -> inventory.setChestplate(item);
                case LEGGINGS -> inventory.setLeggings(item);
                case BOOTS -> inventory.setBoots(item);
                case OFFHAND -> inventory.setItemInOffHand(item);
            }

            return;
        }

        inventory.addItem(item);
    }

    private void runCommands(Player player, Kit kit) {

        if (kit.getCommands() == null || kit.getCommands().isEmpty()) {
            return;
        }

        String playerName = player.getName();

        for (String command : kit.getCommands().values()) {

            if (command == null || command.isBlank()) {
                continue;
            }

            String parsed = command
                    .replace("%player%", playerName)
                    .replace("{player}", playerName);

            if (parsed.startsWith("/")) {
                parsed = parsed.substring(1);
            }

            String finalCommand = parsed;

            Bukkit.getGlobalRegionScheduler().run(plugin, task ->
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            finalCommand
                    )
            );
        }
    }

    private record ParsedKitMenuTitle(String category, int page) {
    }

    private enum EquipmentSlotType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        OFFHAND
    }
}