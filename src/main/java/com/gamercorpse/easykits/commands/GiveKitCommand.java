package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveKitCommand {

    private final EasyKits plugin;

    public GiveKitCommand(EasyKits plugin) {

        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender,
                           String[] args) {

        if (!sender.hasPermission("easykits.admin")) {

            MessageUtil.send(sender,
                    plugin.getConfig()
                            .getString("messages.no-permission"));

            return true;
        }

        if (args.length < 3) {

            MessageUtil.send(sender,
                    "<red>Usage: /easykits give <player> <kit>");

            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);

        if (target == null) {

            MessageUtil.send(sender,
                    "<red>Player not found.");

            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[2]);

        if (kit == null) {

            MessageUtil.send(sender,
                    "<red>Kit not found.");

            return true;
        }

        for (ItemStack item : kit.getItems()) {

            if (item == null) {
                continue;
            }

            target.getInventory().addItem(item.clone());
        }

        MessageUtil.send(sender,
                "<green>Given kit successfully.");

        return true;
    }
}