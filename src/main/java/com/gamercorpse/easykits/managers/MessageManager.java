package com.gamercorpse.easykits.managers;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.utils.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MessageManager {

    private final EasyKits plugin;

    public MessageManager(EasyKits plugin) {

        this.plugin = plugin;
    }

    public void send(CommandSender sender,
                     String message) {

        String prefix = plugin.getConfig()
                .getString("messages.prefix", "");

        Component component = ColorUtil.color(
                prefix + message
        );

        sender.sendMessage(component);
    }
}