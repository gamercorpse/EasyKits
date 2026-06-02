package com.gamercorpse.easykits.gui;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemEditorMenu {

    public static final String TITLE_PREFIX = "Edit Item: ";

    private final EasyKits plugin;

    public ItemEditorMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, String kitId, String itemKey, KitItem kitItem) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                TITLE_PREFIX + kitId + ":" + itemKey
        );

        inventory.setItem(10, button(Material.NAME_TAG, "Edit Display Name"));
        inventory.setItem(11, button(Material.BOOK, "Edit Lore"));
        inventory.setItem(12, button(Material.STONE, "Edit Material"));
        inventory.setItem(13, button(Material.CHEST, "Edit Amount"));
        inventory.setItem(14, button(Material.PAPER, "Edit Custom Model Data"));
        inventory.setItem(15, button(Material.ENCHANTED_BOOK, "Edit Enchantments"));
        inventory.setItem(16, button(kitItem.isUnbreakable() ? Material.LIME_DYE : Material.GRAY_DYE, "Toggle Unbreakable"));
        inventory.setItem(30, button(Material.ITEM_FRAME, "Edit Item Flags"));

        ItemStack preview = ItemBuilder.build(kitItem);

        if (preview == null || preview.getType().isAir()) {
            preview = button(Material.BARRIER, "Invalid Item Preview");
        }

        inventory.setItem(22, preview);

        inventory.setItem(45, button(Material.ARROW, "Back"));
        inventory.setItem(49, button(Material.EMERALD_BLOCK, "Save Kit"));
        inventory.setItem(53, button(Material.BARRIER, "Delete Item"));

        player.openInventory(inventory);
    }

    private ItemStack button(Material material, String name) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of("Click to edit"));
            item.setItemMeta(meta);
        }

        return item;
    }
}