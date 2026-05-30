package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand {

    private final EasyKits plugin;

    public DeleteKitCommand(EasyKits plugin) {

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

        if (args.length < 2) {

            MessageUtil.send(sender,
                    "<red>Usage: /easykits delete <id>");

            return true;
        }

        String id = args[1];

        if (!plugin.getKitManager().exists(id)) {

            MessageUtil.send(sender,
                    "<red>Kit not found.");

            return true;
        }

        plugin.getKitManager().deleteKit(id);

        MessageUtil.send(sender,
                "<red>Deleted kit <white>" + id + "</white>.");

        return true;
    }
}