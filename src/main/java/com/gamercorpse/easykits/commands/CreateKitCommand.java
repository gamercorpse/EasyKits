package com.gamercorpse.easykits.commands;

import com.gamercorpse.easykits.EasyKits;
import com.gamercorpse.easykits.models.Kit;
import org.bukkit.command.CommandSender;

public class CreateKitCommand {

    private final EasyKits plugin;

    public CreateKitCommand(EasyKits plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage("Usage: /kits create <kit>");
            return true;
        }

        String id = args[1].toLowerCase();

        if (plugin.getKitManager().exists(id)) {
            sender.sendMessage("A kit with that ID already exists.");
            return true;
        }

        Kit kit = new Kit(id);

        kit.setDisplayName(id);
        kit.setPermission("");
        kit.setCooldown(0);
        kit.setOneTime(false);

        kit.setCategory("default");

        kit.setIconMaterial("CHEST");
        kit.setIconModelData(0);

        kit.setSlot(getNextAvailableSlot());

        plugin.getKitManager().save(kit);

        sender.sendMessage("Created kit '" + id + "'.");
        sender.sendMessage("Edit it with /kits edit " + id);

        return true;
    }

    private int getNextAvailableSlot() {

        int slot = 0;

        while (isSlotUsed(slot)) {
            slot++;
        }

        return slot;
    }

    private boolean isSlotUsed(int slot) {

        for (Kit kit : plugin.getKitManager().getKits().values()) {
            if (kit.getSlot() == slot) {
                return true;
            }
        }

        return false;
    }
}