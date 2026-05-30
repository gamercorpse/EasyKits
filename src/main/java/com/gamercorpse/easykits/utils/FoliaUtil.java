package com.gamercorpse.easykits.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaUtil {

    private final Plugin plugin;
    private final boolean folia;

    public FoliaUtil(Plugin plugin) {

        this.plugin = plugin;
        this.folia = checkFolia();
    }

    private boolean checkFolia() {

        try {

            Class.forName(
                    "io.papermc.paper.threadedregions.RegionizedServer"
            );

            return true;

        } catch (ClassNotFoundException ignored) {

            return false;
        }
    }

    public void runGlobal(Runnable runnable) {

        if (folia) {

            Bukkit.getGlobalRegionScheduler().run(
                    plugin,
                    task -> runnable.run()
            );

            return;
        }

        Bukkit.getScheduler().runTask(
                plugin,
                runnable
        );
    }

    public void runEntity(Entity entity,
                          Runnable runnable) {

        if (folia) {

            entity.getScheduler().run(
                    plugin,
                    task -> runnable.run(),
                    null
            );

            return;
        }

        Bukkit.getScheduler().runTask(
                plugin,
                runnable
        );
    }

    public boolean isFolia() {
        return folia;
    }
}