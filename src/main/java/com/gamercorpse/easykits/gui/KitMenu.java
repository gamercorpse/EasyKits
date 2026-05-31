package com.gamercorpse.easykits.gui;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KitMenu {

    public static final String MENU_TITLE = "<gradient:#00ff99:#00ccff>Easy Kits</gradient>";

    private final EasyKits plugin;

    public KitMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                ColorUtil.color(MENU_TITLE)
        );

        List<Kit> kits = new ArrayList<>(
                plugin.getKitManager().getKits().values()
        );

        kits.sort(Comparator.comparingInt(Kit::getSlot));

        for (Kit kit : kits) {

            if (kit.getPermission() != null &&
                    !kit.getPermission().isEmpty() &&
                    !player.hasPermission(kit.getPermission())) {
                continue;
            }

            int slot = kit.getSlot();

            if (slot < 0 || slot >= inventory.getSize()) {
                continue;
            }

            Material material;

            try {
                material = Material.valueOf(
                        kit.getIconMaterial().toUpperCase()
                );
            } catch (Exception ex) {
                material = Material.CHEST;
            }

            ItemStack item = new ItemStack(material);

            ItemMeta meta = item.getItemMeta();

            if (meta != null) {

                meta.displayName(
                        ColorUtil.color(
                                kit.getDisplayName() != null
                                        ? kit.getDisplayName()
                                        : kit.getId()
                        )
                );

                List<String> lore = new ArrayList<>();

                lore.add("<gray>Left-click to claim this kit");
                lore.add("<gray>Right-click to preview this kit");
                lore.add("");
                lore.add("<yellow>Cooldown: <white>" + kit.getCooldown() + "s");

                if (kit.getPermission() != null &&
                        !kit.getPermission().isEmpty()) {

                    lore.add(
                            player.hasPermission(kit.getPermission())
                                    ? "<green>You have access"
                                    : "<red>No access"
                    );
                }

                List<net.kyori.adventure.text.Component> components = new ArrayList<>();

                for (String line : lore) {
                    components.add(ColorUtil.color(line));
                }

                meta.lore(components);

                if (kit.getIconModelData() > 0) {
                    meta.setCustomModelData(kit.getIconModelData());
                }

                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                item.setItemMeta(meta);
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }
}