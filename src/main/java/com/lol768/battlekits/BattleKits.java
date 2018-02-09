package com.lol768.battlekits;

import com.lol768.battlekits.commands.CommandKitCreation;
import com.lol768.battlekits.listeners.RespawnKit;
import com.lol768.battlekits.listeners.SignHandler;
import com.lol768.battlekits.utilities.ConfigAccessor;
import com.lol768.battlekits.utilities.Converter;
import com.lol768.battlekits.utilities.Localisation;
import com.lol768.battlekits.utilities.PM;
import com.lol768.battlekits.listeners.RestrictionEvents;
import com.lol768.battlekits.listeners.PlayerReward;
import com.lol768.battlekits.listeners.InstaSoup;
import com.lol768.battlekits.listeners.DeathEvent;
import com.lol768.battlekits.commands.CommandRefillAll;
import com.lol768.battlekits.commands.CommandBattleKits;
import com.lol768.battlekits.commands.CommandSoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static com.lol768.battlekits.utilities.Localisation.m;

public class BattleKits extends JavaPlugin {

    public static net.milkbowl.vault.economy.Economy economy = null;
    public CommandBattleKits cbk = new CommandBattleKits(this);
    public PM PM = new PM(this);
    public ConfigAccessor global;
    public ConfigAccessor kits;
    public ConfigAccessor kitHistory;
    public static String legacyType;

    @Override
    public void onEnable() {
        legacyType = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("R")[0];
        if (!createDataDirectory()) {
            getLogger().severe("Couldn't create BattleKits data folder. Shutting down...");
            this.setEnabled(false);
        }
        makeConfigs();
        startMetrics();
        if (!global.getConfig().getBoolean("settings.converted")) {
            new Converter(this).convert();
        }
    }

    @Override
    public void onDisable() {
        kitHistory.saveConfig();
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    private void startMetrics() {
        Metrics metrics = new Metrics(this);
    }

    /**
     * Multi-world config accessor
     *
     * String path - The setting path to look for (e.g.
     * settings.disable-xp)
     * Player p - Player to get world from
     * Object defaultValue - If empty, use this value
     * @return Object - result
     */
    public Object checkSetting(String path, Player p, Object defaultValue) {
        if (global.getConfig().contains(p.getWorld().getName() + "." + path)) {
            return global.getConfig().get(p.getWorld().getName() + "." + path);
        } else {
            if (global.getConfig().contains(path)) {
                return global.getConfig().get(path);
            } else {
                return defaultValue;
            }
        }

    }

    /**
     * Multi-world config accessor
     *
     * String path - The setting path to look for (e.g.
     * settings.disable-xp)
     * Player p - Player to get world from
     * Object defaultValue - If empty, use this
     * @return Object - resultant list
     */
    public List<String> checkList(String path, Player p) {
        if (global.getConfig().contains(p.getWorld().getName() + "." + path)) {
            return global.getConfig().getStringList(p.getWorld().getName() + "." + path);
        } else {
            if (global.getConfig().contains(path)) {
                return global.getConfig().getStringList(path);
            } else {
                return null;
            }
        }

    }

    /**
     * Multi-world config accessor -- accepts world name instead of Player
     *
     * String path - The setting path to look for (e.g.settings.disable-xp)
     * path String world - World to check
     * Object defaultValue - If empty, use this
     * @return Object - result
     */
    public Object checkSetting(String path, String world, Object defaultValue) {
        if (global.getConfig().contains(world + "." + path)) {
            //We have an override

            return global.getConfig().get(world + "." + path);
        } else {
            if (global.getConfig().contains(path)) {
                return global.getConfig().get(path);
            } else {
                return defaultValue;
            }
        }

    }

    public boolean buy(double amount, String name) {
        Player p = Bukkit.getPlayer(name);
        net.milkbowl.vault.economy.EconomyResponse r = economy.withdrawPlayer(name, amount);

        if (r.transactionSuccess()) {
            PM.notify(p, m("economyOk", amount, r.balance));
            return true;
        } else {
            PM.warn(p, m("economyFail", amount, r.balance));
        }
        return false;
    }

    public boolean buyNeutral(double amount, String name) {
        Player p = Bukkit.getPlayer(name);
        net.milkbowl.vault.economy.EconomyResponse r = economy.withdrawPlayer(name, amount);

        if (r.transactionSuccess()) {
            PM.notify(p, m("economyOk", amount, r.balance));
            return true;

        } else {
            PM.notify(p, m("economyFail", amount, r.balance));
        }
        return false;
    }

    private boolean createDataDirectory() {
        File file = this.getDataFolder();
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                // failed to create the non existent directory, so failed
                return false;
            }
        }
        return true;
    }

    public void makeConfigs() {
        kits = new ConfigAccessor(this, "kits.yml");
        global = new ConfigAccessor(this, "global.yml");
        kitHistory = new ConfigAccessor(this, "kitHistory.yml");
        kits.reloadConfig();
        global.reloadConfig();
        kitHistory.reloadConfig();
        postStartup();
        loadMessagesCache();
    }

    public void loadMessagesCache() {
        String locale = global.getConfig().getString("settings.locale", "en");
        File messagesFile = new File(getDataFolder(), "messages.yml");
        FileConfiguration c = new YamlConfiguration();
        if (messagesFile.exists()) {
            try {
                debug("Messages.yml found, loading...");
                c.load(messagesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            InputStream localeStream = this.getResource("messages_LOCALE.yml".replace("LOCALE", locale));
            try {
                debug("Loading locale file.", locale);
                c.load(new BufferedReader(new InputStreamReader(localeStream)));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        for (String key : c.getKeys(false)) {
            Localisation.messagesCache.put(key, c.getString(key));
        }
        PM.prefix = "&7[&6"+global.getConfig().getString("brand")+"&7] ";
    }

    public void postStartup() {
        getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new RespawnKit(this), this);
        getServer().getPluginManager().registerEvents(new PlayerReward(this), this);
        getServer().getPluginManager().registerEvents(new InstaSoup(this), this);

        getCommand("toolkit").setExecutor(new CommandKitCreation(this));
        getCommand("fillall").setExecutor(new CommandRefillAll(this));
        getCommand("soup").setExecutor(new CommandSoup(this));
        getCommand("battlekits").setExecutor(cbk);

        if (global.getConfig().getBoolean("signs.enabled")) {
            getServer().getPluginManager().registerEvents(new SignHandler(this), this);
        }

        if (global.getConfig().getBoolean("settings.enable-restrictions")) {
            getServer().getPluginManager().registerEvents(new RestrictionEvents(this), this);
            debug("Restrictions enabled. Use permissions to setup");
        } else {
            debug("Not enabling restrictions due to config setting");
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            debug("Vault found.");
            setupEconomy();

        } else {
            debug("Couldn't find Vault. Economy disabled for now.");
        }
    }

    public ItemStack setColor(ItemStack item, int color) {
        if (!item.getType().toString().contains("LEATHER"))  {
            return item;
        }
        LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
        im.setColor(Color.fromRGB(color));
        item.setItemMeta(im);
        return item;
    }

    public void debug(Object... o) {
        getLogger().log(Level.INFO, Arrays.toString(o));
    }
}
