package com.lol768.battlekits.commands;

import com.lol768.battlekits.BattleKits;
import com.lol768.battlekits.utilities.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lol768.battlekits.utilities.Localisation.m;

public class CommandKitCreation implements CommandExecutor {

    public BattleKits plugin;
    public String kits = "kits.";

    public CommandKitCreation(BattleKits battleKits) {
        plugin = battleKits;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("battlekits.createkit") && sender instanceof Player && args.length >= 1) {
            Player p = (Player) sender;
            if (args[0].equalsIgnoreCase("debug")) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.PM.notify(p, m("kitPasteOk", paste("#Debug paste created by "+sender.getName()+"\n"+plugin.kits.getConfig().saveToString()))));
                return true;
            }
            createKit(p, args[0],  args);
            return true;
        }
        return false;
    }

    public void createKit(Player p, String name, String[] args) { // this is so messy.
        YamlConfiguration kitConfig = new YamlConfiguration();
        kitConfig.set(kits+name+".active-in", "all");
        if (args.length >= 2 && args[1].equalsIgnoreCase("2")) {
            kitConfig.set(kits+name+".format", 2);
            kitConfig.set(kits+name+".items", Serializer.itemStackArrayToBase64(p.getInventory().getContents()));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.PM.notify(p, m("kitCreateOk", paste("#Copy the contents below this line to your kits.yml "+kitConfig.saveToString()))));
            return;
        }
        ItemStack helmet = p.getInventory().getHelmet();
        ItemStack chestPlate = p.getInventory().getChestplate();
        ItemStack leggings = p.getInventory().getLeggings();
        ItemStack boots = p.getInventory().getBoots();
        if (helmet != null) {
            kitConfig.set(kits+name+".items.helmet", helmet.getType().toString());
            if (!helmet.getEnchantments().isEmpty()) {
                kitConfig.set(kits+name+".items.helmetEnchant", getEnch(helmet));
            }
        }
        if (chestPlate != null) {
            kitConfig.set(kits+name+".items.chestplate", p.getInventory().getChestplate().getType().toString());
            if (!chestPlate.getEnchantments().isEmpty()) {
                kitConfig.set(kits+name+".items.chestplateEnchant", getEnch(chestPlate));
            }
        }
        if (leggings != null) {
            kitConfig.set(kits+name+".items.leggings", p.getInventory().getLeggings().getType().toString());
            if (!leggings.getEnchantments().isEmpty()) {
                kitConfig.set(kits+name+".items.leggingsEnchant", getEnch(leggings));
            }
        }
        if (boots != null) {
            kitConfig.set(kits+name+".items.boots", p.getInventory().getBoots().getType().toString());
            if (!boots.getEnchantments().isEmpty()) {
                kitConfig.set(kits+name+".items.bootsEnchant", getEnch(boots));
            }

        }
        if (p.getInventory().getItemInOffHand().getType() != Material.AIR) { //bukkit returns air instead of null
            kitConfig.set(kits+name+".items.offhand", p.getInventory().getItemInOffHand().getType().toString());
        }

        for (int i = 0; i < 35; i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item != null) {
                String s = item.getType().toString();
                s+= item.getAmount() > 1 ? ":"+item.getAmount(): "";
                if (!item.getType().toString().contains("POTION")) {
                    s+= getEnch(item);
                } else {
                    PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                    for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                        s+= " "+potionEffect.getType().getName()+":"+potionEffect.getDuration()+":"+potionEffect.getAmplifier();
                    }
                }
                kitConfig.set(kits+name+".items."+String.valueOf(i), s);
            }
        }
        for (int i = 0; i < 35; i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
                kitConfig.set(kits+name+".items."+"names."+String.valueOf(i), item.getItemMeta().getDisplayName().replace(ChatColor.COLOR_CHAR, '&'));
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.PM.notify(p, m("kitCreateOk", paste("#Copy the contents below this line to your kits.yml "+kitConfig.saveToString()))));

    }

    public String getEnch(ItemStack i) {
        String s = "";
        for (Enchantment en : i.getEnchantments().keySet()) {
            s+= " "+en.getName()+":"+i.getEnchantmentLevel(en);
        }
        return s.equals("") ? "" : s.substring(1);
    }

    public String paste(String s) {
        try {
            URL youareell = new URL("https://hasteb.in/documents");
            HttpsURLConnection con = (HttpsURLConnection) youareell.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setDoOutput(true);
            try (BufferedOutputStream B = new BufferedOutputStream(con.getOutputStream())) {
                B.write(s.getBytes("utf8"));
                B.flush();
            }
            int i = con.getResponseCode();
            final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String datLine = br.readLine();
            return parseData(datLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String parseData(String s) {
        plugin.debug("Haste link:", s);
        Matcher m = Pattern.compile("^\\{\"key\":\"(.*)\"}$").matcher(s);
        if (m.matches()) {
            return "https://hasteb.in/"+m.group(1)+".kit";
        } else return "unknownPaste";
    }
}