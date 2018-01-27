package com.lol768.battlekits.listeners;

import com.lol768.battlekits.BattleKits;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InstaSoup implements Listener {

    public BattleKits plugin;

    public InstaSoup(BattleKits p) {
        this.plugin = p;
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material mat = p.getInventory().getItemInMainHand().getType();
            if (mat != null && mat == Material.valueOf(plugin.global.getConfig().getString("instant-soup-drink.type"))) {
                if ((boolean) plugin.checkSetting("settings.instant-soup-drink", p, false)) {
                    if (plugin.checkSetting("instant-soup-drink.replenish-type", p, "hunger").equals("hunger")) {
                        ItemStack bowl = new ItemStack(Material.BOWL, 1);

                        if (p.getFoodLevel() == 20) {//If food is full, stop the code.
                            event.setCancelled(true);
                            p.sendMessage(ChatColor.RED + "Your food is already full!");
                            return;
                        }

                        if (p.getFoodLevel() + 6 <= 20) { //Only add some hunger back on
                            event.setCancelled(true);
                            clearHand(p);
                            p.setFoodLevel(p.getFoodLevel() + 6);
                        }

                        if (p.getFoodLevel() + 6 > 20) { //Hunger close to max, so refill it
                            event.setCancelled(true);
                            clearHand(p);
                            p.setFoodLevel(20);
                        }
                    }

                    if (plugin.checkSetting("instant-soup-drink.replenish-type", p, "hunger").equals("health")) {
                        ItemStack bowl = new ItemStack(Material.BOWL, 1);

                        if (p.getHealth() == p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {//If health is full, stop the code.
                            event.setCancelled(true);
                            p.sendMessage(ChatColor.RED + "You are already full health!");
                            return;
                        }

                        if (p.getHealth() + 6 <= 20) { //Only add some health back on
                            event.setCancelled(true);
                            clearHand(p);
                            p.setHealth(p.getHealth() + 6);
                        }

                        if (p.getHealth() + 6 > 20) { //Health close to max, so refill it
                            event.setCancelled(true);
                            clearHand(p);
                            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                        }
                    }
                }
            }
        }
    }

    public void clearHand(Player p) {
        if ((boolean) plugin.checkSetting("settings.instant-soup-drink.remove-bowl", p, false)) {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            return;
        }
        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
    }
}
