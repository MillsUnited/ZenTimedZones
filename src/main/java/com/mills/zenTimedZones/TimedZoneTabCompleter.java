package com.mills.zenTimedZones;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimedZoneTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if (sender.hasPermission(Main.permission)) {
            if (args.length == 1) {
                return StringUtil.copyPartialMatches(args[0], Arrays.asList("reload", "start", "setspawn", "time", "stop"), new ArrayList<>());
            }
        }
        return new ArrayList<>();
    }
}
