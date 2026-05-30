package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CreateKitCommand {

    private final EasyKits plugin;

    public CreateKitCommand(EasyKits plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {

            MessageUtil.send(sender,
                    plugin.getConfig().getString("messages.player-only"));
            return true;
        }

        if (!player.hasPermission("easykits.admin")) {

            MessageUtil.send(player,
                    plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length < 2) {

            MessageUtil.send(player,
                    "<red>Usage: /easykits create <id>");
            return true;
        }

        String id = args[1];

        if (plugin.getKitManager().exists(id)) {

            MessageUtil.send(player,
                    "<red>That kit already exists.");
            return true;
        }

        Kit kit = new Kit(id);

        Map<String, KitItem> items = new HashMap<>();

        int index = 0;

        for (ItemStack item : player.getInventory().getContents()) {

            if (item == null || item.getType().isAir()) continue;

            KitItem kitItem = new KitItem();

            kitItem.setMaterial(item.getType().name());
            kitItem.setAmount(item.getAmount());

            items.put("item_" + index, kitItem);
            index++;
        }

        kit.setItems(items);

        plugin.getKitManager().saveKit(kit);

        MessageUtil.send(player,
                "<green>Created kit <white>" + id + "</white>.");

        return true;
    }
}