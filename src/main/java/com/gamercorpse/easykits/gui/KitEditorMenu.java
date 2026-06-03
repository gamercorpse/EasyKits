package com.gamercorpse.easykits.gui;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.sessions.EditorSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class KitEditorMenu {

    public static final String TITLE_PREFIX = "Edit Kit: ";

    private final EasyKits plugin;

    public KitEditorMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
    }

    public void open(Player player, Kit kit) {

        Inventory inventory = Bukkit.createInventory(null, 54, TITLE_PREFIX + kit.getId());

        inventory.setItem(10, button(Material.NAME_TAG, "Edit Display Name"));
        inventory.setItem(12, button(Material.PAPER, "Edit Permission"));
        inventory.setItem(14, button(Material.CLOCK, "Edit Cooldown"));
        inventory.setItem(16, button(kit.isOneTime() ? Material.LIME_DYE : Material.GRAY_DYE, "Toggle One-Time"));

        inventory.setItem(20, button(Material.BOOKSHELF, "Edit Category"));
        inventory.setItem(22, button(Material.ANVIL, "Edit Items"));
        inventory.setItem(24, button(Material.COMMAND_BLOCK, "Edit Commands"));

        inventory.setItem(28, icon(kit));
        inventory.setItem(30, button(Material.HOPPER, "Edit Menu Slot"));

        inventory.setItem(32, button(Material.ENDER_EYE, "Preview Kit"));
        inventory.setItem(34, button(Material.CHEST, "Import Inventory"));

        inventory.setItem(49, button(Material.EMERALD_BLOCK, "Save Kit"));
        inventory.setItem(53, button(Material.BARRIER, "Delete Kit"));

        if (EditorSession.get(player.getUniqueId()) == null) {
            EditorSession.create(player.getUniqueId(), kit);
        }

        player.openInventory(inventory);
    }

    private ItemStack icon(Kit kit) {

        ItemStack item = KitItem.deserializeItem(kit.getSerializedIcon());

        if (item == null || item.getType().isAir()) {

            Material material = Material.CHEST;

            if (kit.getIconMaterial() != null) {
                try {
                    material = Material.valueOf(kit.getIconMaterial().toUpperCase());
                } catch (Exception ignored) {
                }
            }

            item = new ItemStack(material);

            ItemMeta meta = item.getItemMeta();

            if (meta != null && kit.getIconModelData() > 0) {
                meta.setCustomModelData(kit.getIconModelData());
                item.setItemMeta(meta);
            }
        }

        item = item.clone();
        item.setAmount(1);

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("Kit Icon");
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

    public static Map<String, KitItem> collectItems(Inventory inventory) {

        Map<String, KitItem> items = new HashMap<>();

        int index = 0;

        for (int slot = 32; slot <= 40; slot++) {

            ItemStack item = inventory.getItem(slot);

            if (item == null || item.getType().isAir()) {
                continue;
            }

            items.put("item_" + index, KitItem.fromItemStack(item));
            index++;
        }

        return items;
    }

    public static void importPlayerInventory(Player player, Kit kit) {

        Map<String, KitItem> items = new HashMap<>();

        int index = 0;

        for (ItemStack item : player.getInventory().getStorageContents()) {

            if (index >= 9) {
                break;
            }

            if (item == null || item.getType().isAir()) {
                continue;
            }

            items.put("item_" + index, KitItem.fromItemStack(item.clone()));
            index++;
        }

        kit.setItems(items);

        kit.setHelmet(toKitItemOrNull(player.getInventory().getHelmet()));
        kit.setChestplate(toKitItemOrNull(player.getInventory().getChestplate()));
        kit.setLeggings(toKitItemOrNull(player.getInventory().getLeggings()));
        kit.setBoots(toKitItemOrNull(player.getInventory().getBoots()));
        kit.setOffhand(toKitItemOrNull(player.getInventory().getItemInOffHand()));
    }

    public static Map<String, KitItem> collectPlayerInventory(Player player) {

        Map<String, KitItem> items = new HashMap<>();

        int index = 0;

        for (ItemStack item : player.getInventory().getStorageContents()) {

            if (index >= 9) {
                break;
            }

            if (item == null || item.getType().isAir()) {
                continue;
            }

            items.put("item_" + index, KitItem.fromItemStack(item.clone()));
            index++;
        }

        return items;
    }

    private static KitItem toKitItemOrNull(ItemStack item) {

        if (item == null || item.getType().isAir()) {
            return null;
        }

        return KitItem.fromItemStack(item.clone());
    }
}