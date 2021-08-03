package me.gustav.armorchange;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorChangeListener implements Listener {

    //Event Priority is highest because other plugins might cancel the events before we check.
    @EventHandler(priority = EventPriority.HIGHEST)
    public final void inventoryClick(final InventoryClickEvent e) {
        // Context is using player's inventory, meaning we can interact
        // with the player's armor equipment
        if (e.getInventory().getType() == InventoryType.CRAFTING) {
            if (e.getAction() == InventoryAction.NOTHING) return;

            if (!(e.getWhoClicked() instanceof Player)) return;
            final Player player = (Player) e.getWhoClicked();

            ArmorChangeEvent armorChangeEvent;

            // We either shift click the item(MOVE_TO_OTHER_INVENTORY)
            // or use keys to switch (HOTBAR_SWAP)
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
//                if (isAirOrNull(e.getCurrentItem())) return; // Unnessacary check since we check if Action == InventoryAction.NOTHING

                final ItemStack item = e.getCurrentItem();

                ArmorType armorType = ArmorType.matchType(item);
                if (armorType == null) return;

                // The slot is occupied, meaning you cant shift click item.
                if (!isAirOrNull(player.getInventory().getItem(armorType.getSlot()))) return;

                armorChangeEvent = new ArmorChangeEvent(player, ArmorChangeEvent.EquipMethod.SHIFT_CLICK, armorType, null, item);

            } else if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
                ItemStack opposite = player.getInventory().getItem(e.getHotbarButton());
                ItemStack item = e.getCurrentItem();

                ArmorType armorType = ArmorType.matchType(opposite);

                armorChangeEvent = new ArmorChangeEvent(player,
                        ArmorChangeEvent.EquipMethod.HOTBAR_SWAP,
                        armorType,
                        item,
                        opposite);
            } else if (e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.PICKUP_HALF) {
                ItemStack item = e.getCurrentItem();
                ArmorType armorType = ArmorType.matchType(item);
                if (armorType == null) return;

                if (e.getSlotType() != InventoryType.SlotType.ARMOR) return;

                armorChangeEvent = new ArmorChangeEvent(player, ArmorChangeEvent.EquipMethod.DRAG, armorType, item, null);
            } else if (e.getAction() == InventoryAction.PLACE_ALL || e.getAction() == InventoryAction.PLACE_ONE) {
                ItemStack item = e.getCursor();
                ArmorType armorType = ArmorType.matchType(item);
                if (armorType == null) return;

                if (e.getSlotType() != InventoryType.SlotType.ARMOR) return;

                armorChangeEvent = new ArmorChangeEvent(player, ArmorChangeEvent.EquipMethod.DRAG, armorType, null, item);
            } else if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                ItemStack cursor = e.getCursor();
                ItemStack current = e.getCurrentItem();
                ArmorType armorType = ArmorType.matchType(current);
                if (armorType == null) return;

                if (e.getSlotType() != InventoryType.SlotType.ARMOR) return;

                armorChangeEvent = new ArmorChangeEvent(player, ArmorChangeEvent.EquipMethod.PICK_DROP, armorType, current, cursor);
            } else return;

            Bukkit.getServer().getPluginManager().callEvent(armorChangeEvent);
            if (armorChangeEvent.isCancelled()) e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final ItemStack hand = e.getItem();
            if (isAirOrNull(hand)) return;

            final ArmorType type = ArmorType.matchType(hand);
            if (type != null) {
                // TODO make a QUICK_SWAP feature here
                final ItemStack armorSlot = e.getPlayer().getInventory().getItem(type.getSlot());
                if (!isAirOrNull(armorSlot)) return;

                ArmorChangeEvent armorChangeEvent = new ArmorChangeEvent(e.getPlayer(),
                        ArmorChangeEvent.EquipMethod.HOTBAR,
                        type,
                        null,
                        hand);

                Bukkit.getServer().getPluginManager().callEvent(armorChangeEvent);
                if (armorChangeEvent.isCancelled()) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent e) {
        ArmorType type = ArmorType.matchType(e.getBrokenItem());

        if (type != null) {
            Player p = e.getPlayer();
            ArmorChangeEvent armorEquipEvent = new ArmorChangeEvent(p, ArmorChangeEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if (armorEquipEvent.isCancelled()) {
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));

                p.getInventory().setItem(type.getSlot(), i);
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent e) {
        if (e.getKeepInventory()) return;

        Player p = e.getEntity();
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (!isAirOrNull(i)) {
                Bukkit.getServer().getPluginManager().callEvent(new ArmorChangeEvent(p, ArmorChangeEvent.EquipMethod.DEATH, ArmorType.matchType(i), i, null));
                // No way to cancel a death event.
            }
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
