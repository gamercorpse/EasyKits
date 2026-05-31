package com.gamercorpse.easykits.gui;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.ColorUtil;
import com.gamercorpse.easykits.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PreviewMenu {

    public static final String TITLE_PREFIX = "Preview Kit: ";

    private final EasyKits plugin;

    public PreviewMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Kit kit) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                TITLE_PREFIX + kit.getId()
        );

        int slot = 0;

        if (kit.getItems() != null) {

            for (KitItem kitItem : kit.getItems().values()) {

                if (slot >= 45) {
                    break;
                }

                ItemStack item = ItemBuilder.build(kitItem);

                if (item == null || item.getType().isAir()) {
                    continue;
                }

                inventory.setItem(slot, item);
                slot++;
            }
        }

        inventory.setItem(49, createButton(
                Material.ARROW,
                "<yellow>Back to Kits"
        ));

        inventory.setItem(53, createButton(
                Material.EMERALD_BLOCK,
                "<green>Claim Kit"
        ));

        player.openInventory(inventory);
    }

    private ItemStack createButton(Material material, String name) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(ColorUtil.color(name));
            item.setItemMeta(meta);
        }

        return item;
    }
}