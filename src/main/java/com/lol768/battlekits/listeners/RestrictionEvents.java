package com.lol768.battlekits.listeners;

import com.lol768.battlekits.BattleKits;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class RestrictionEvents implements Listener {

    private BattleKits plugin;

    public RestrictionEvents(BattleKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void pnItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(!e.getPlayer().hasPermission("battlekits.bypassrestriction.disable-dropping-items") && !e.isCancelled());
    }

    @EventHandler
    public void craftItemEvent(CraftItemEvent e) {
        if (!e.getWhoClicked().hasPermission("battlekits.bypassrestriction.disable-crafting") && !e.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(EntityPickupItemEvent e) {
        if (!(e instanceof Player)) return;
        if (!e.getEntity().hasPermission("battlekits.bypassrestriction.disable-pickup-items") && !e.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void bpe(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("battlekits.bypassrestriction.disable-block-place") && !e.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if (!e.getEntity().hasPermission("battlekits.bypassrestriction.disable-player-xp-drop")) {
            e.setDroppedExp(0);
        }
        if (!e.getEntity().hasPermission("battlekits.bypassrestriction.disable-player-drops-on-death")) {
            e.getDrops().clear();
        }
        if (!e.getEntity().hasPermission("battlekits.bypassrestriction.hide-death-messages")) {
            e.setDeathMessage(null);
        }

    }

    @EventHandler
    public void mobDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            if (!(e.getEntity() instanceof Player) && e.getEntity().getKiller() instanceof Player) {
                Player p = e.getEntity().getKiller();
                if (!p.hasPermission("battlekits.bypassrestriction.disable-mob-xp")) {
                    e.setDroppedExp(0);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().hasPermission("battlekits.bypassrestriction.disable-block-xp") && !e.isCancelled()) {
            e.setExpToDrop(0);
        }
        if (!e.getPlayer().hasPermission("battlekits.bypassrestriction.disable-block-break") && !e.isCancelled()) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void invInteract(InventoryClickEvent e) {
        if (!e.getWhoClicked().hasPermission("battlekits.bypassrestriction.disable-inventory-click") && !e.isCancelled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        if (p.hasPermission("battlekits.disableFoodChange")) {
            e.setCancelled(true);
        }
    }
}
