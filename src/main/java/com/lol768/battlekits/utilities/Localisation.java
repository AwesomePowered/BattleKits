package com.lol768.battlekits.utilities;

import com.lol768.battlekits.BattleKits;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

public class Localisation {

    public static HashMap<String, String> messagesCache = new HashMap<>();

    public static String m(String key, Object... args) {
        return String.format(messagesCache.get(key), args);
    }

}
