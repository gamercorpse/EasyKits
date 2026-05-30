package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import com.gamercorpse.easykits.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KitCommand implements CommandExecutor {

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

            MessageUtil.send(sender, "<yellow>EasyKits Commands:");
            MessageUtil.send(sender, "<gray>/easykits create <id>");
            MessageUtil.send(sender, "<gray>/easykits delete <id>");
            MessageUtil.send(sender, "<gray>/easykits give <player> <kit>");
            MessageUtil.send(sender, "<gray>/easykits reload");
            MessageUtil.send(sender, "<gray>/easykits <kit>");

            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {

            case "create" -> {
                return new CreateKitCommand(plugin)
                        .execute(sender, args);
            }

            case "delete" -> {
                return new DeleteKitCommand(plugin)
                        .execute(sender, args);
            }

            case "give" -> {
                return new GiveKitCommand(plugin)
                        .execute(sender, args);
            }

            case "reload" -> {
                return new ReloadCommand(plugin)
                        .execute(sender, args);
            }

            default -> {

                Kit kit = plugin.getKitManager().getKit(subCommand);

                if (kit == null) {

                    MessageUtil.send(sender,
                            "<red>Unknown kit.");

                    return true;
                }

                return true;
            }
        }
    }
}