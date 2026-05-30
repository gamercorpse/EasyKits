package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.KitMenu;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenMenuCommand {

    private final EasyKits plugin;

    public OpenMenuCommand(EasyKits plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {

            MessageUtil.send(
                    sender,
                    plugin.getConfig().getString("messages.prefix", ""),
                    "<red>Players only."
            );

            return true;
        }

        new KitMenu(plugin).open(player);

        return true;
    }
}