package com.lol768.battlekits.listeners;

import com.lol768.battlekits.BattleKits;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static com.lol768.battlekits.utilities.Localisation.m;

import java.util.logging.Level;

public class SignHandler implements Listener {

    private BattleKits plugin;

    public SignHandler(BattleKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void signClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST)) {

            Sign s = (Sign) e.getClickedBlock().getState();
            String[] lines = s.getLines();
            if (lines.length > 1 && isSign(lines[0])) {
                e.setCancelled(true);
                if (p.hasPermission("battlekits.sign.use")) {

                    if (!plugin.kits.getConfig().contains("kits." + lines[1])) {
                        if (lines[1].equals("soupFill")) {
                            if (!p.hasPermission("battlekits.soupfill")) {
                                plugin.PM.warn(p, m("genericNoPerm"));
                                return;
                            }
                            boolean rez = true;
                            if (plugin.checkSetting("signs.soupFillCost", p, null) != null && BattleKits.economy != null) {
                                rez = plugin.buyNeutral((Double) plugin.checkSetting("signs.soupFillCost", p, null), p.getName());
                            }
                            if (rez) {
                                for (ItemStack i : p.getInventory().getContents()) {
                                    if (i == null) {
                                        p.getInventory().addItem(new ItemStack(Material.valueOf(plugin.global.getConfig().getString("instant-soup-drink.type", "MUSHROOM_SOUP")), 1));
                                    }
                                }
                            }
                        } else {
                            plugin.PM.warn(p, m("kitNotFound"));
                        }
                    } else {
                        plugin.cbk.supplyKit(p, lines[1], (boolean) plugin.checkSetting("signs.ignore-permissions", p, false), (boolean) plugin.checkSetting("signs.ignore-costs", p, false), (boolean) plugin.checkSetting("signs.ignore-lives-restriction", p, false), (boolean) plugin.checkSetting("signs.ignore-world-restriction", p, false));
                    }
                } else {
                    plugin.PM.warn(p, m("KitSignUsePermMSG"));
                }
            }
        }
    }

    @EventHandler
    public void signEdit(SignChangeEvent e) {
        String[] lines = e.getLines();
        Player p = e.getPlayer();

        if (lines.length > 1 && isSign(lines[0])) {

            if (p.hasPermission("battlekits.sign.create")) {

                if (plugin.kits.getConfig().contains("kits." + lines[1]) || lines[1].equals("soupFill")) {
                    e.setLine(0, ChatColor.translateAlternateColorCodes('&',"&4[&6"+plugin.global.getConfig().getString("brand")+"&4]"));
                    plugin.PM.notify(p, m("kitSignMade"));

                } else {
                    e.getBlock().breakNaturally();
                    plugin.PM.warn(p, m("kitNotFound"));
                }
            } else {
                plugin.PM.warn(p, m("KitSignCreatePermMSG"));
                e.getBlock().breakNaturally();

            }
        }
    }

    public boolean isSign(String s) {
        s = ChatColor.stripColor(s);
        return s.equalsIgnoreCase("["+ChatColor.stripColor(plugin.global.getConfig().getString("brand"))+"]");
    }
}
