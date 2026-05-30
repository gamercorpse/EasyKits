package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.models.KitItem;
import com.gamercorpse.easykits.utils.ItemBuilder;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveKitCommand {

    private final EasyKits plugin;

    public GiveKitCommand(EasyKits plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission("easykits.admin")) {

            MessageUtil.send(
                    sender,
                    plugin.getConfig().getString("messages.prefix", ""),
                    plugin.getConfig().getString("messages.no-permission", "<red>No permission.")
            );

            return true;
        }

        if (args.length < 3) {

            MessageUtil.send(
                    sender,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>Usage: /easykits give <player> <kit>"
            );

            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);

        if (target == null) {

            MessageUtil.send(
                    sender,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>Player not found."
            );

            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[2]);

        if (kit == null) {

            MessageUtil.send(
                    sender,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>Kit not found."
            );

            return true;
        }

        int given = 0;

        if (kit.getItems() != null) {

            for (KitItem kitItem : kit.getItems().values()) {

                ItemStack item = ItemBuilder.build(kitItem);

                if (item == null || item.getType().isAir()) continue;

                target.getInventory().addItem(item);
                given++;
            }
        }

        // Give feedback to sender
        MessageUtil.send(
                sender,
                plugin.getConfig().getString("messages.prefix", ""),
                "<green>Gave kit <white>" + kit.getId() +
                        "</white> to <white>" + target.getName() +
                        "</white> (" + given + " items)."
        );

        return true;
    }
}