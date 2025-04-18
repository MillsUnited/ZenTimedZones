package com.mills.zenTimedZones;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static String prefix;
    private static MessagesManager messagesManager;
    public static String permission;
    private static Main instance;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        messagesManager = new MessagesManager(this);
        prefix = ChatColor.translateAlternateColorCodes('&', getMessagesManager().getMessage("Prefix"));
        permission = Main.getMessagesManager().getMessage("Admin-Permission");

        instance = this;

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TimedZonePlaceholder().register();
        }

        getCommand("timedzone").setExecutor(new TimedZoneCommand());
        getCommand("timedzone").setTabCompleter(new TimedZoneTabCompleter());
    }

    public static Main getInstance() {
        return instance;
    }

    public static MessagesManager getMessagesManager() {
        return messagesManager;
    }
}
