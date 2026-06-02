package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.gui.KitEditorMenu;
import com.gamercorpse.easykits.models.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitCommand implements CommandExecutor, TabCompleter {

    private final EasyKits plugin;

    public KitCommand(EasyKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (args.length == 0) {
            return new OpenMenuCommand(plugin).execute(sender, args);
        }

        switch (args[0].toLowerCase()) {

            case "give" -> {
                return new GiveKitCommand(plugin).execute(sender, args);
            }

            case "create" -> {
                return new CreateKitCommand(plugin).execute(sender, args);
            }

            case "reload" -> {
                return new ReloadCommand(plugin).execute(sender, args);
            }

            case "delete" -> {
                return new DeleteKitCommand(plugin).execute(sender, args);
            }

            case "menu" -> {
                return new OpenMenuCommand(plugin).execute(sender, args);
            }

            case "edit", "editor" -> {

                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Players only.");
                    return true;
                }

                if (!player.hasPermission("easykits.kit.edit")) {
                    player.sendMessage("You do not have permission.");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage("Usage: /" + label + " " + args[0].toLowerCase() + " <kit>");
                    return true;
                }

                Kit kit = plugin.getKitManager().getKit(args[1]);

                if (kit == null) {
                    player.sendMessage("Unknown kit.");
                    return true;
                }

                new KitEditorMenu(plugin).open(player, kit);
                return true;
            }

            case "list" -> {

                StringBuilder sb = new StringBuilder("Kits: ");

                for (Kit kit : plugin.getKitManager().getKits().values()) {
                    sb.append(kit.getId()).append(", ");
                }

                sender.sendMessage(sb.toString());
                return true;
            }

            default -> {
                sender.sendMessage("Unknown subcommand.");
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,
                                      Command command,
                                      String alias,
                                      String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {

            completions.add("give");
            completions.add("create");
            completions.add("reload");
            completions.add("delete");
            completions.add("menu");
            completions.add("list");

            if (!(sender instanceof Player player)
                    || player.hasPermission("easykits.kit.edit")) {

                completions.add("edit");
                completions.add("editor");
            }

            return filter(completions, args[0]);
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("give")) {

                for (Player online : Bukkit.getOnlinePlayers()) {
                    completions.add(online.getName());
                }

                return filter(completions, args[1]);
            }

            if (args[0].equalsIgnoreCase("delete")
                    || args[0].equalsIgnoreCase("edit")
                    || args[0].equalsIgnoreCase("editor")) {

                for (Kit kit : plugin.getKitManager().getKits().values()) {
                    completions.add(kit.getId());
                }

                return filter(completions, args[1]);
            }

            return completions;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {

            for (Kit kit : plugin.getKitManager().getKits().values()) {
                completions.add(kit.getId());
            }

            return filter(completions, args[2]);
        }

        return completions;
    }

    private List<String> filter(List<String> completions, String input) {

        if (input == null || input.isEmpty()) {
            return completions;
        }

        List<String> filtered = new ArrayList<>();

        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(input.toLowerCase())) {
                filtered.add(completion);
            }
        }

        return filtered;
    }
}