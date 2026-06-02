package com.gamercorpse.easykits.gui;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ItemListEditorMenu {

    public static final String TITLE_PREFIX = "Edit Kit Items: ";

    private final EasyKits plugin;

    public ItemListEditorMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Kit kit) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                TITLE_PREFIX + kit.getId()
        );

        List<Map.Entry<String, KitItem>> items = getSortedItems(kit);

        int slot = 0;

        for (Map.Entry<String, KitItem> entry : items) {

            if (slot >= 36) {
                break;
            }

            ItemStack item = ItemBuilder.build(entry.getValue());

            if (item == null || item.getType().isAir()) {
                item = button(Material.BARRIER, "Invalid Item");
            }

            inventory.setItem(slot, item);
            slot++;
        }

        inventory.setItem(45, equipmentButton("helmet", kit.getHelmet(), Material.LEATHER_HELMET));
        inventory.setItem(46, equipmentButton("chestplate", kit.getChestplate(), Material.LEATHER_CHESTPLATE));
        inventory.setItem(47, equipmentButton("leggings", kit.getLeggings(), Material.LEATHER_LEGGINGS));
        inventory.setItem(48, equipmentButton("boots", kit.getBoots(), Material.LEATHER_BOOTS));
        inventory.setItem(50, equipmentButton("offhand", kit.getOffhand(), Material.SHIELD));

        inventory.setItem(49, button(Material.ARROW, "Back"));
        inventory.setItem(53, button(Material.EMERALD_BLOCK, "Add Cursor Item"));

        player.openInventory(inventory);
    }

    public static List<Map.Entry<String, KitItem>> getSortedItems(Kit kit) {

        List<Map.Entry<String, KitItem>> items = new ArrayList<>();

        if (kit.getItems() != null) {
            items.addAll(kit.getItems().entrySet());
        }

        items.sort(Comparator.comparing(Map.Entry::getKey));

        return items;
    }

    public static String getItemKeyBySlot(Kit kit, int slot) {

        List<Map.Entry<String, KitItem>> items = getSortedItems(kit);

        if (slot < 0 || slot >= items.size()) {
            return null;
        }

        return items.get(slot).getKey();
    }

    public static String nextItemKey(Kit kit) {

        int index = 0;

        while (kit.getItems().containsKey("item_" + index)) {
            index++;
        }

        return "item_" + index;
    }

    private ItemStack equipmentButton(String name, KitItem kitItem, Material emptyMaterial) {

        if (kitItem == null) {
            return button(emptyMaterial, "Edit " + name + " (empty)");
        }

        ItemStack item = ItemBuilder.build(kitItem);

        if (item == null || item.getType().isAir()) {
            return button(emptyMaterial, "Edit " + name + " (invalid)");
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add("");
            lore.add("Click to edit " + name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack button(Material material, String name) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }
}