package com.gamercorpse.easykits.utils;

import com.gamercorpse.easykits.EasyKits;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    private MessageUtil() {

    }

    public static void send(CommandSender sender,
                            String message) {

        String prefix = EasyKits.getInstance()
                .getConfig()
                .getString(
                        "messages.prefix",
                        ""
                );

        Component component = ColorUtil.color(
                prefix + message
        );

        sender.sendMessage(component);
    }
}