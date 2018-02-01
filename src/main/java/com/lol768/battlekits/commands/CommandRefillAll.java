package com.lol768.battlekits.commands;

import com.lol768.battlekits.BattleKits;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static com.lol768.battlekits.utilities.Localisation.m;

public class CommandRefillAll implements CommandExecutor {

    public BattleKits plugin;

    public CommandRefillAll(BattleKits p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("fillall")) {
            if(!(sender instanceof Player)) {
                plugin.PM.warn(sender, m("cmdPlayerOnly"));
                return true;
            }
            Player p = (Player) sender;
            ItemStack[] inv = p.getInventory().getContents();
            boolean gotBowl = false;
            if (sender.hasPermission("battlekits.use.fillall")) {
                //Get array of itemstack
                for (ItemStack slot : inv) {
                    if (slot != null && slot.getType() == Material.BOWL) { //Check for NPE
                        gotBowl = true;
                        slot.setType(Material.valueOf(plugin.global.getConfig().getString("instant-soup-drink.type")));
                    }
                }

                if (!gotBowl) {
                    plugin.PM.warn(p, m("noBowls"));
                    return true;
                }
                return true;

            } else {
                plugin.PM.warn(p, m("cmdPermMSG"));
                return true;
            }
        }

        return false;
    }
}