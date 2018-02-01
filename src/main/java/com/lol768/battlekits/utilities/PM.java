package com.lol768.battlekits.utilities;

import com.lol768.battlekits.BattleKits;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PM {

    public BattleKits plugin;
    public String prefix;

    public PM(BattleKits instance) {
        plugin = instance;
    }

    public void message(Player player, String message) {
        if (message.startsWith("&h")) {
            message = message.substring(2);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+message));
        }
    }

    public void message(CommandSender player, String message) {
        if (message.startsWith("&h")) {
            message = message.substring(2);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+message));
        }
    }

    /**
     * Method to keep messages consistent Sends a red coloured warning to the
     * supplied player
     *
     * @param player - The player to send the warning to
     * @param message - The message to send them
     */
    public void warn(Player player, String message) {
        message(player, message);
    }

    /**
     * Method to keep messages consistent Sends a red coloured warning to the
     * supplied sender Supports console & uncast player (CommandSender)
     *
     * @param sender - The CommandSender to send the warning to
     * @param message - The message to send them
     */
    public void warn(CommandSender sender, String message) {
        message(sender, message);
    }

    /**
     * Method to keep messages consistent Sends a yellow notification to the
     * supplied player
     *
     * @param player - The player to send the message to
     * @param message - The message to send them
     */
    public void notify(CommandSender player, String message) {
        message(player, message);
    }

    /**
     * Method to keep messages consistent Sends a yellow notification to the
     * supplied sender
     *
     * @param player - The player to send the message to
     * @param message - The message to send them
     */
    public void notify(Player player, String message) {
        message(player, message);
    }
}
