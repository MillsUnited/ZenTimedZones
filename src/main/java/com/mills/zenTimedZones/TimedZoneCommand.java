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
                player.sendMessage(Main.prefix + "you don't have permission to use this!");
                return false;
            }

            if (args.length == 0) {
                player.sendMessage(Main.prefix + "Usage: /zentimedzones <reload|setspawn|start|time|stop>");
                return false;
            }

            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "reload":
                    Main.getInstance().reloadConfig();
                    player.sendMessage(Main.prefix + "Configuration reloaded!");
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
                    player.sendMessage(Main.prefix + "Spawn location saved to config.");
                    return true;

                case "start":
                    if (args.length < 3) {
                        player.sendMessage(Main.prefix + "Usage: /zentimedzones start <region> <time(seconds)>");
                        return false;
                    }

                    String regionName = args[1];
                    int timeLimit;
                    try {
                        timeLimit = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Main.prefix + "Time must be a valid number.");
                        return false;
                    }

                    boolean success = TimedZoneManager.startTime(regionName, timeLimit, player);
                    if (success) {
                        player.sendMessage(Main.prefix + "Passthrough enabled for region " + regionName + " for " + timeLimit + " seconds.");
                    }
                    return true;

                case "time":
                    if (args.length < 2) {
                        player.sendMessage(Main.prefix + "Usage: /zentimedzones time <region>");
                        return false;
                    }

                    String checkRegion = args[1];
                    if (!TimedZoneManager.isRegionActive(checkRegion)) {
                        player.sendMessage(Main.prefix + "Region '" + checkRegion + "' is currently closed.");
                    } else {
                        String timeLeft = TimedZoneManager.getTimeLeft(checkRegion);
                        player.sendMessage(Main.prefix + "Region '" + checkRegion + "' is open for " + timeLeft + ".");
                    }
                    return true;

                case "stop":
                    if (args.length < 2) {
                        player.sendMessage(Main.prefix + "Usage: /zentimedzones stop <region>");
                        return false;
                    }

                    String selectedRegion = args[1];
                    if (!TimedZoneManager.isRegionActive(selectedRegion)) {
                        player.sendMessage(Main.prefix + "Region '" + selectedRegion + "' is not currently active.");
                    } else {
                        TimedZoneManager.stopTime(selectedRegion, player);
                    }
                    return true;

                default:
                    player.sendMessage(Main.prefix + "Usage: /zentimedzones <reload|setspawn|start|time|stop>");
                    return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be run by a player.");
            return false;
        }
    }
}
