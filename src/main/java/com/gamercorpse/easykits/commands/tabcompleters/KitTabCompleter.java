package com.gamercorpse.easykits.commands.tabcompleters;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitTabCompleter implements TabCompleter {

    private final EasyKits plugin;

    public KitTabCompleter(EasyKits plugin) {

        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {

            completions.add("create");
            completions.add("delete");
            completions.add("give");
            completions.add("reload");

            for (Kit kit : plugin.getKitManager().getKits().values()) {
                completions.add(kit.getId());
            }

            return completions;
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("delete")) {

                for (Kit kit : plugin.getKitManager().getKits().values()) {
                    completions.add(kit.getId());
                }

                return completions;
            }

            if (args[0].equalsIgnoreCase("give")) {

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    completions.add(player.getName());
                }

                return completions;
            }
        }

        if (args.length == 3) {

            if (args[0].equalsIgnoreCase("give")) {

                for (Kit kit : plugin.getKitManager().getKits().values()) {
                    completions.add(kit.getId());
                }

                return completions;
            }
        }

        return Collections.emptyList();
    }
}