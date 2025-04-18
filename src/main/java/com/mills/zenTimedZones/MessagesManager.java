package com.mills.zenTimedZones;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MessagesManager {

    private Main main;

    public MessagesManager(Main main) {
        this.main = main;
        createMessageConfig();
    }

    private File messagesFile;
    private FileConfiguration messagesConfig;

    public void createMessageConfig() {
        messagesFile = new File(main.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            main.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            main.getLogger().severe("Could not save messages.yml");
            e.printStackTrace();
        }
    }

    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        saveMessagesConfig();
    }

    public String getMessage(String path) {
        if (messagesConfig.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(messagesConfig.getString(path)));
        }
        return "null";
    }
}
