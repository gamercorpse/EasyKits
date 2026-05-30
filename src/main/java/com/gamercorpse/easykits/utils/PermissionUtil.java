package com.gamercorpse.easykits.utils;

import org.bukkit.command.CommandSender;

public class PermissionUtil {

    private PermissionUtil() {

    }

    public static boolean hasPermission(CommandSender sender,
                                        String permission) {

        return sender.hasPermission(permission);
    }
}