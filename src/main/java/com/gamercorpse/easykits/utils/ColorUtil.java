package com.gamercorpse.easykits.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ColorUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component color(String text) {

        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        return MINI_MESSAGE.deserialize(convertLegacyColors(text));
    }

    public static String convertLegacyColors(String text) {

        if (text == null || text.isEmpty()) {
            return "";
        }

        return text
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&k", "<obfuscated>")
                .replace("&l", "<bold>")
                .replace("&m", "<strikethrough>")
                .replace("&n", "<underlined>")
                .replace("&o", "<italic>")
                .replace("&r", "<reset>")
                .replace("&A", "<green>")
                .replace("&B", "<aqua>")
                .replace("&C", "<red>")
                .replace("&D", "<light_purple>")
                .replace("&E", "<yellow>")
                .replace("&F", "<white>")
                .replace("&K", "<obfuscated>")
                .replace("&L", "<bold>")
                .replace("&M", "<strikethrough>")
                .replace("&N", "<underlined>")
                .replace("&O", "<italic>")
                .replace("&R", "<reset>");
    }
}