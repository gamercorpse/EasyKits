package com.gamercorpse.easykits.listeners;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.KitMenu;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.ItemBuilder;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KitMenuListener implements Listener {

    private final EasyKits plugin;

    public KitMenuListener(EasyKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (event.getView().title() == null) return;

        String title = event.getView().title().toString();

        if (!title.contains("Easy Kits")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType().isAir()) {
            return;
        }

        int slot = event.getSlot();

        Kit clickedKit = null;

        for (Kit kit : plugin.getKitManager().getKits().values()) {

            if (kit.getSlot() == slot) {
                clickedKit = kit;
                break;
            }
        }

        if (clickedKit == null) {
            return;
        }

        if (clickedKit.getPermission() != null &&
                !clickedKit.getPermission().isEmpty() &&
                !player.hasPermission(clickedKit.getPermission())) {

            MessageUtil.send(
                    player,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>You do not have permission for this kit."
            );

            return;
        }

        int given = 0;

        if (clickedKit.getItems() != null) {

            for (KitItem kitItem : clickedKit.getItems().values()) {

                ItemStack item = ItemBuilder.build(kitItem);

                if (item == null || item.getType().isAir()) continue;

                player.getInventory().addItem(item);
                given++;
            }
        }

        MessageUtil.send(
                player,
                plugin.getConfig().getString("messages.prefix", ""),
                "<green>You claimed kit <white>" +
                        clickedKit.getId() +
                        "</white>."
        );

        player.closeInventory();
    }
}