package me.gustav.armorchange;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;

public class DispenseArmorListener implements Listener {

    @EventHandler
    public void dispenseArmorEvent(BlockDispenseArmorEvent event) {
        ArmorType type = ArmorType.matchType(event.getItem());
        if (type != null) {
            if (event.getTargetEntity() instanceof Player) {
                Player p = (Player) event.getTargetEntity();
                ArmorChangeEvent armorChangeEvent = new ArmorChangeEvent(p, ArmorChangeEvent.EquipMethod.DISPENSER, type, null, event.getItem());
                Bukkit.getServer().getPluginManager().callEvent(armorChangeEvent);
                if (armorChangeEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
