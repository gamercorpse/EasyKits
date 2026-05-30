package com.gamercorpse.easykits.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    private MessageUtil() {
    }

    public static void send(CommandSender sender,
                            String prefix,
                            String message) {

        Component component = ColorUtil.color(
                prefix + message
        );

        sender.sendMessage(component);
    }
}