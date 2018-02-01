package com.lol768.battlekits.commands;

import com.lol768.battlekits.BattleKits;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.lol768.battlekits.utilities.Localisation.m;

public class CommandSoup implements CommandExecutor {

    public BattleKits plugin;

    public CommandSoup(BattleKits p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("soup")) {
            if (!(sender instanceof Player)) {
                plugin.PM.warn(sender, m("cmdPlayerOnly"));
                return true;
            }
            Player p = (Player) sender;
            ItemStack i = p.getInventory().getItemInMainHand();

            if (sender.hasPermission("battlekits.use.soup")) {
                //TODO: @MapleFighter -- implement whole inventory
                if (i.getType() != Material.BOWL) {
                    plugin.PM.warn(p, m("noBowls"));
                    return true;

                } else {
                    i.setType(Material.valueOf(plugin.global.getConfig().getString("instant-soup-drink.type")));
                    return true;
                }

            } else {
                plugin.PM.warn(sender, m("cmdPermMSG"));
                return true;
            }

        }
        return false;
    }
}