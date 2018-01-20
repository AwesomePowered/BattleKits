package com.lol768.battlekits.utilities;

import com.lol768.battlekits.BattleKits;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Bat;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

public class Converter {

    private FileConfiguration kitsConfiguration;
    private BattleKits plugin;
    String[] armors = {"helmet","chestplate", "leggings", "boots"};

    public Converter(BattleKits plugin) {
        this.plugin = plugin;
        kitsConfiguration = plugin.kits.getConfig();
    }

    public void convert() {
        plugin.debug();
        plugin.debug("Starting conversion, legacyType:", BattleKits.legacyType);
        for (String className : kitsConfiguration.getConfigurationSection("kits").getKeys(false)) {

            for (String armor : armors) {
                String armorType = kitsConfiguration.getString("kits."+className+".items."+armor);
                if (armorType != null && !armorType.contains("_")) {
                    plugin.kits.getConfig().set("kits."+className+".items."+armor, armorType.toUpperCase()+"_"+armor.toUpperCase());
                }
            }

            for (int slot = 0; slot <= 35; slot++) {
                if (kitsConfiguration.contains("kits." + className + ".items." + slot)) {
                    String slotContents = kitsConfiguration.getString("kits." + className + ".items." + slot);
                    String[] item = kitsConfiguration.getString("kits." + className + ".items." + slot).split(" ")[0].split(":");
                    if (StringUtils.isNumeric(item[0])) {
                        String newMaterial = slotContents.replaceFirst(item[0], MaterialMap.getFromLegacyId(item[0]));
                        plugin.debug("Converting:"+className, "Slot:"+slot,"id:"+item[0],"enum:"+newMaterial);
                        plugin.kits.getConfig().set("kits."+className+".items."+slot, newMaterial);
                    }
                }
            }
        }
        plugin.global.getConfig().set("settings.converted", true);
        plugin.kits.saveConfig();
        plugin.global.saveConfig();
        plugin.debug("Conversion complete!");
    }

}
