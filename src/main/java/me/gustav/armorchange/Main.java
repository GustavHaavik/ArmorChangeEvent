package me.gustav.armorchange;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the listeners
        Bukkit.getPluginManager().registerEvents(new ArmorChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new DispenseArmorListener(), this);
    }
}
