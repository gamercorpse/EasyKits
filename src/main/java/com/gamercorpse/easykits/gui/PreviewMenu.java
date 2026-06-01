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

                if (slot >= 36) {
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

        setPreviewEquipment(inventory, 45, kit.getHelmet(), Material.LEATHER_HELMET, "<yellow>Helmet");
        setPreviewEquipment(inventory, 46, kit.getChestplate(), Material.LEATHER_CHESTPLATE, "<yellow>Chestplate");
        setPreviewEquipment(inventory, 47, kit.getLeggings(), Material.LEATHER_LEGGINGS, "<yellow>Leggings");
        setPreviewEquipment(inventory, 48, kit.getBoots(), Material.LEATHER_BOOTS, "<yellow>Boots");
        setPreviewEquipment(inventory, 50, kit.getOffhand(), Material.SHIELD, "<yellow>Offhand");

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

    private void setPreviewEquipment(Inventory inventory,
                                     int slot,
                                     KitItem kitItem,
                                     Material fallback,
                                     String emptyName) {

        if (kitItem == null) {
            inventory.setItem(slot, createButton(fallback, emptyName + " <gray>(empty)"));
            return;
        }

        ItemStack item = ItemBuilder.build(kitItem);

        if (item == null || item.getType().isAir()) {
            inventory.setItem(slot, createButton(fallback, emptyName + " <gray>(empty)"));
            return;
        }

        inventory.setItem(slot, item);
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