package com.mills.zenTimedZones;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TimedZoneCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission(Main.permission)) {
                player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                        Main.getMessagesManager().getMessage("Messages.no-permission")));
                return false;
            }

            if (args.length == 0) {
                player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                        Main.getMessagesManager().getMessage("Messages.invalid-command-ussage")));
                return false;
            }

            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "reload":
                    Main.getInstance().reloadConfig();
                    Main.getMessagesManager().reloadMessagesConfig();
                    player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                            Main.getMessagesManager().getMessage("Messages.config-reload")));
                    return true;

                case "setspawn":
                    Location loc = player.getLocation();
                    FileConfiguration config = Main.getInstance().getConfig();
                    config.set("Spawn.World-Name", loc.getWorld().getName());
                    config.set("Spawn.X", loc.getX());
                    config.set("Spawn.Y", loc.getY());
                    config.set("Spawn.Z", loc.getZ());
                    config.set("Spawn.Yaw", loc.getYaw());
                    config.set("Spawn.Pitch", loc.getPitch());
                    Main.getInstance().saveConfig();
                    player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                            Main.getMessagesManager().getMessage("Messages.set-spawn")));
                    return true;

                case "start":
                    if (args.length < 3) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.start-command-ussage")));
                        return false;
                    }

                    String regionName = args[1];
                    int timeLimit;
                    try {
                        timeLimit = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.invalid-time-number")));
                        return false;
                    }

                    boolean success = TimedZoneManager.startTime(regionName, timeLimit, player);
                    if (success) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.start-timedzone")
                                        .replace("<region>", regionName)
                                        .replace("<time>", String.valueOf(timeLimit))));
                    }
                    return true;

                case "time":
                    if (args.length < 2) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.time-command-ussage")));
                        return false;
                    }

                    String checkRegion = args[1];
                    if (!TimedZoneManager.isRegionActive(checkRegion)) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.close-region-regionclosed")
                                        .replace("<region>", checkRegion)));
                    } else {
                        int timeLeft = TimedZoneManager.getTimeLeft(checkRegion);
                        String timeLeftMessage = timeLeft + " second(s)";
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.time-region-active")
                                        .replace("<region>", checkRegion)
                                        .replace("<time>", timeLeftMessage)));
                    }
                    return true;

                case "stop":
                    if (args.length < 2) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.stop-command-ussage")));
                        return false;
                    }

                    String selectedRegion = args[1];
                    if (!TimedZoneManager.isRegionActive(selectedRegion)) {
                        player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                                Main.getMessagesManager().getMessage("Messages.stop-region-not-active")
                                        .replace("<region>", selectedRegion)));
                    } else {
                        TimedZoneManager.stopTime(selectedRegion, player);
                    }
                    return true;

                default:
                    player.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&',
                            Main.getMessagesManager().getMessage("Messages.invalid-command-ussage")));
                    return false;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    Main.getMessagesManager().getMessage("Messages.console-running-command")));
            return false;
        }
    }
}