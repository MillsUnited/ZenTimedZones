package com.mills.zenTimedZones;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&a&lZenPVP &8» &7");
    public static String permission = "timedzones.admin";
    private static Main instance;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        instance = this;

        getCommand("timedzone").setExecutor(new TimedZoneCommand());
        getCommand("timedzone").setTabCompleter(new TimedZoneTabCompleter());
    }

    public static Main getInstance() {
        return instance;
    }
}
