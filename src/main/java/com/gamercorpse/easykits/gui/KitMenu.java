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
import java.util.Set;
import java.util.TreeSet;

public class KitMenu {

    public static final String MENU_TITLE = "<gradient:#00ff99:#00ccff>Easy Kits</gradient>";

    public static final String CATEGORY_TITLE = "<gradient:#00ff99:#00ccff>Easy Kits Categories</gradient>";
    public static final String CATEGORY_TITLE_TEXT = "Easy Kits Categories";

    public static final String KIT_TITLE_PREFIX = "Easy Kits: ";
    public static final int KITS_PER_PAGE = 45;

    private final EasyKits plugin;

    public KitMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {

        Set<String> categories = getVisibleCategories(player);

        if (categories.size() <= 1) {
            String category = categories.isEmpty() ? "default" : categories.iterator().next();
            open(player, category, 0);
            return;
        }

        openCategories(player);
    }

    public void openCategories(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                ColorUtil.color(CATEGORY_TITLE)
        );

        List<String> categories = new ArrayList<>(getVisibleCategories(player));

        int slot = 0;

        for (String category : categories) {

            if (slot >= 45) {
                break;
            }

            inventory.setItem(slot, categoryIcon(category, countVisibleKits(player, category)));
            slot++;
        }

        player.openInventory(inventory);
    }

    public void open(Player player, String category, int page) {

        if (category == null || category.isBlank()) {
            category = "default";
        }

        category = category.toLowerCase();

        if (page < 0) {
            page = 0;
        }

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                KIT_TITLE_PREFIX + category + " - Page " + (page + 1)
        );

        List<Kit> kits = getVisibleKits(player, category);

        int start = page * KITS_PER_PAGE;
        int end = Math.min(start + KITS_PER_PAGE, kits.size());

        for (int index = start; index < end; index++) {
            Kit kit = kits.get(index);
            int displaySlot = index - start;
            inventory.setItem(displaySlot, kitIcon(player, kit));
        }

        if (getVisibleCategories(player).size() > 1) {
            inventory.setItem(45, simpleButton(Material.BOOKSHELF, "<yellow>Categories"));
        }

        if (page > 0) {
            inventory.setItem(48, simpleButton(Material.ARROW, "<yellow>Previous Page"));
        }

        if (hasPage(player, category, page + 1)) {
            inventory.setItem(50, simpleButton(Material.ARROW, "<yellow>Next Page"));
        }

        player.openInventory(inventory);
    }

    public Set<String> getVisibleCategories(Player player) {

        Set<String> categories = new TreeSet<>();

        for (Kit kit : plugin.getKitManager().getKits().values()) {

            if (!canSee(player, kit)) {
                continue;
            }

            String category = kit.getCategory();

            if (category == null || category.isBlank()) {
                category = "default";
            }

            categories.add(category.toLowerCase());
        }

        return categories;
    }

    public List<Kit> getVisibleKits(Player player, String category) {

        List<Kit> kits = new ArrayList<>();

        if (category == null || category.isBlank()) {
            category = "default";
        }

        String requestedCategory = category.toLowerCase();

        for (Kit kit : plugin.getKitManager().getKits().values()) {

            if (!canSee(player, kit)) {
                continue;
            }

            String kitCategory = kit.getCategory();

            if (kitCategory == null || kitCategory.isBlank()) {
                kitCategory = "default";
            }

            if (!kitCategory.equalsIgnoreCase(requestedCategory)) {
                continue;
            }

            kits.add(kit);
        }

        kits.sort(
                Comparator.comparingInt(Kit::getSlot)
                        .thenComparing(Kit::getId)
        );

        return kits;
    }

    public Kit getKitByDisplaySlot(Player player, String category, int page, int displaySlot) {

        if (displaySlot < 0 || displaySlot >= KITS_PER_PAGE) {
            return null;
        }

        List<Kit> kits = getVisibleKits(player, category);

        int index = page * KITS_PER_PAGE + displaySlot;

        if (index < 0 || index >= kits.size()) {
            return null;
        }

        return kits.get(index);
    }

    public boolean hasPage(Player player, String category, int page) {

        if (page < 0) {
            return false;
        }

        List<Kit> kits = getVisibleKits(player, category);

        return page * KITS_PER_PAGE < kits.size();
    }

    private boolean canSee(Player player, Kit kit) {

        return kit.getPermission() == null ||
                kit.getPermission().isEmpty() ||
                player.hasPermission(kit.getPermission());
    }

    private int countVisibleKits(Player player, String category) {
        return getVisibleKits(player, category).size();
    }

    private ItemStack categoryIcon(String category, int count) {

        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(ColorUtil.color("<yellow>" + category));
            meta.lore(List.of(
                    ColorUtil.color("<gray>Kits: <white>" + count),
                    ColorUtil.color("<gray>Click to open")
            ));
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack kitIcon(Player player, Kit kit) {

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
            lore.add("<yellow>Category: <white>" + kit.getCategory());
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

        return item;
    }

    private ItemStack simpleButton(Material material, String name) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(ColorUtil.color(name));
            item.setItemMeta(meta);
        }

        return item;
    }
}