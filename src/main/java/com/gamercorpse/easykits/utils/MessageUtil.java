package com.gamercorpse.easykits.utils;

import com.gamercorpse.easykits.EasyKits;
import org.bukkit.command.CommandSender;

public class MessageUtil {

    public static void send(CommandSender sender, String message) {

        String prefix = EasyKits.getInstance()
                .getConfig()
                .getString("messages.prefix", "");

        sender.sendMessage(
                ColorUtil.color(prefix + message)
        );
    }
}