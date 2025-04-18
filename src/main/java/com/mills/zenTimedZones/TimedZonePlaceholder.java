package com.mills.zenTimedZones;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimedZonePlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "timedzones";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mills";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // %timedzones_timeleft_<region>%

        if (identifier.startsWith("timeleft_")) {
            String region = identifier.substring("timeleft_".length());

            int seconds = TimedZoneManager.getTimeLeft(region);

            if (seconds == -1) return ChatColor.translateAlternateColorCodes('&', Main.getMessagesManager().getMessage("Placeholder.region_closed"));

            return DynamicTimeUtil.formatDuration(seconds * 1000L);

        }

        return null;
    }


}
