package com.lol768.battlekits.listeners;

import com.lol768.battlekits.BattleKits;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static com.lol768.battlekits.utilities.Localisation.m;

public class DeathEvent implements Listener {

    private BattleKits plugin;

    public DeathEvent(BattleKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArmourDeplete(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            for (ItemStack armour : p.getInventory().getArmorContents()) {
                if (armour != null && armour.hasItemMeta()) {
                    if (armour.getItemMeta().hasDisplayName()) {
                        if (armour.getItemMeta().getDisplayName().startsWith(ChatColor.RESET + "" + ChatColor.RESET)) {
                            armour.setDurability((short) 0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
        if (mainHand != null && mainHand.getItemMeta() != null && mainHand.getItemMeta().getDisplayName() != null) {
            if (mainHand.getItemMeta().getDisplayName().startsWith(ChatColor.RESET + "" + ChatColor.RESET)) {
                mainHand.setDurability((short) 0);
            }
        }
    }

    /**
     * Death event that resets lives so that Player can get kits again
     *
     * @param event - EntityDamageEvent
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity() != null) {
            final Player p = event.getEntity();
            if (plugin.kitHistory.getConfig().contains("dead." + p.getName())) {

                if ((boolean) plugin.checkSetting("settings.once-per-life", p, false)) {
                    plugin.kitHistory.getConfig().set("dead." + p.getName(), null);
                }

                if ((boolean) plugin.checkSetting("settings.show-kit-info-on-respawn", p, false)) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.PM.notify(p, m("kitAvailable")), 60L);

                }

            }
        }
    }
}
