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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KitMenuListener implements Listener {

    private final EasyKits plugin;

    public KitMenuListener(EasyKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        String title = PlainTextComponentSerializer.plainText()
                .serialize(event.getView().title());

        if (title.equalsIgnoreCase("Easy Kits")) {
            handleKitMenuClick(event);
            return;
        }

        if (title.startsWith(PreviewMenu.TITLE_PREFIX)) {
            handlePreviewMenuClick(event, title);
        }
    }

    private void handleKitMenuClick(InventoryClickEvent event) {

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        Kit clickedKit = getKitBySlot(slot);

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

    private Kit getKitBySlot(int slot) {

        for (Kit kit : plugin.getKitManager().getKits().values()) {

            if (kit.getSlot() == slot) {
                return kit;
            }
        }

        return null;
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

        MessageUtil.send(
                player,
                plugin.getConfig().getString("messages.prefix", ""),
                "<green>You claimed kit <white>" +
                        kit.getId() +
                        "</white>."
        );

        player.closeInventory();
    }

    private void runCommands(Player player, Kit kit) {

        if (kit.getCommands() == null || kit.getCommands().isEmpty()) {
            return;
        }

        for (String command : kit.getCommands().values()) {

            if (command == null || command.isBlank()) {
                continue;
            }

            String parsed = command
                    .replace("%player%", player.getName())
                    .replace("{player}", player.getName());

            if (parsed.startsWith("/")) {
                parsed = parsed.substring(1);
            }

            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    parsed
            );
        }
    }
}