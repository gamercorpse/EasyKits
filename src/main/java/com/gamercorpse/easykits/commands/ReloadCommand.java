package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    private final EasyKits plugin;

    public ReloadCommand(EasyKits plugin) {
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

        plugin.getKitManager().load();

        MessageUtil.send(
                sender,
                plugin.getConfig().getString("messages.prefix", ""),
                plugin.getConfig().getString("messages.reloaded", "<green>Kits reloaded.")
        );

        return true;
    }
}