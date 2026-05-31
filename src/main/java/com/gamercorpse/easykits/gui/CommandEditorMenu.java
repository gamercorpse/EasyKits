package com.gamercorpse.easykits.gui;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
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

public class CommandEditorMenu {

    public static final String TITLE_PREFIX = "Edit Commands: ";

    private final EasyKits plugin;

    public CommandEditorMenu(EasyKits plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Kit kit) {

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                TITLE_PREFIX + kit.getId()
        );

        List<Map.Entry<String, String>> commands = getSortedCommands(kit);

        int slot = 0;

        for (Map.Entry<String, String> entry : commands) {

            if (slot >= 45) {
                break;
            }

            inventory.setItem(
                    slot,
                    button(
                            Material.COMMAND_BLOCK,
                            "Command " + (slot + 1),
                            "Click to remove",
                            entry.getValue()
                    )
            );

            slot++;
        }

        inventory.setItem(49, button(
                Material.ARROW,
                "Back",
                "Return to kit editor"
        ));

        inventory.setItem(53, button(
                Material.EMERALD_BLOCK,
                "Add Command",
                "Click to type a command in chat",
                "Use %player% for the player name"
        ));

        player.openInventory(inventory);
    }

    public static List<Map.Entry<String, String>> getSortedCommands(Kit kit) {

        List<Map.Entry<String, String>> commands = new ArrayList<>();

        if (kit.getCommands() != null) {
            commands.addAll(kit.getCommands().entrySet());
        }

        commands.sort(Comparator.comparing(Map.Entry::getKey));

        return commands;
    }

    public static String nextCommandKey(Kit kit) {

        int index = 0;

        while (kit.getCommands().containsKey("cmd_" + index)) {
            index++;
        }

        return "cmd_" + index;
    }

    private ItemStack button(Material material, String name, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);

            if (lore != null && lore.length > 0) {
                meta.setLore(List.of(lore));
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}