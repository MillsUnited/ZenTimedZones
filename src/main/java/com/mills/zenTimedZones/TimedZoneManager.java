package com.mills.zenTimedZones;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class TimedZoneManager {

    private static final Map<String, Long> activeRegions = new HashMap<>();
    private static final Map<String, BukkitTask> regionTasks = new HashMap<>();

    public static boolean startTime(String region, Integer time, Player sender) {
        boolean success = setPassthroughAllow(sender.getWorld(), region, sender);

        if (!success) return false;

        long endTime = System.currentTimeMillis() + (time * 1000L);
        String key = region.toLowerCase();

        activeRegions.put(key, endTime);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            setPassthroughDeny(sender.getWorld(), region, sender);
            activeRegions.remove(key);
            regionTasks.remove(key);
            sender.sendMessage(Main.prefix + "Passthrough has been disabled for region " + region);
        }, time * 20L);

        regionTasks.put(key, task);
        return true;
    }

    public static void stopTime(String region, Player sender) {
        String key = region.toLowerCase();

        BukkitTask task = regionTasks.remove(key);
        if (task != null) task.cancel();

        activeRegions.remove(key);
        setPassthroughDeny(sender.getWorld(), region, sender);
        sender.sendMessage(Main.prefix + "Force stopped region '" + region + "'. Passthrough disabled.");
    }

    private static boolean setPassthroughAllow(World bukkitWorld, String regionId, Player sender) {
        if (bukkitWorld == null) {
            sender.sendMessage(Main.prefix + "World does not exist.");
            return false;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(bukkitWorld));

        if (regions == null) {
            sender.sendMessage(Main.prefix + "No regions found for world: " + bukkitWorld.getName());
            return false;
        }

        ProtectedRegion region = regions.getRegion(regionId);
        if (region == null) {
            sender.sendMessage(Main.prefix + "Region '" + regionId + "' not found!");
            return false;
        }

        region.setFlag(Flags.ENTRY, StateFlag.State.ALLOW);
        return true;
    }

    private static void setPassthroughDeny(World bukkitWorld, String regionId, Player sender) {
        if (bukkitWorld == null) {
            sender.sendMessage(Main.prefix + "World does not exist.");
            return;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(bukkitWorld));

        if (regions == null) {
            sender.sendMessage(Main.prefix + "No regions found for world: " + bukkitWorld.getName());
            return;
        }

        ProtectedRegion region = regions.getRegion(regionId);
        if (region == null) {
            sender.sendMessage(Main.prefix + "Region '" + regionId + "' not found!");
            return;
        }

        FileConfiguration config = Main.getInstance().getConfig();

        String worldName = config.getString("Spawn.World-Name");
        double x = config.getDouble("Spawn.X");
        double y = config.getDouble("Spawn.Y");
        double z = config.getDouble("Spawn.Z");
        float yaw = (float) config.getDouble("Spawn.Yaw");
        float pitch = (float) config.getDouble("Spawn.Pitch");

        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Location spawn = new Location(world, x, y, z, yaw, pitch);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (region.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())) {
                    player.teleport(spawn);
                }
            }
        }

        region.setFlag(Flags.ENTRY, StateFlag.State.DENY);
    }

    public static String getTimeLeft(String region) {
        Long endTime = activeRegions.get(region.toLowerCase());
        if (endTime == null) return null;

        long millisLeft = endTime - System.currentTimeMillis();
        if (millisLeft <= 0) return null;

        long seconds = millisLeft / 1000;
        return seconds + " second(s)";
    }

    public static boolean isRegionActive(String region) {
        return activeRegions.containsKey(region.toLowerCase());
    }
}
