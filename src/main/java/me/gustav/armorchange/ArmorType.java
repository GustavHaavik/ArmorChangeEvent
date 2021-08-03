package me.gustav.armorchange;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
    HELMET(EquipmentSlot.HEAD), CHESTPLATE(EquipmentSlot.CHEST), LEGGINGS(EquipmentSlot.LEGS), BOOTS(EquipmentSlot.FEET);

    private final EquipmentSlot slot;

    ArmorType(EquipmentSlot slot) {
        this.slot = slot;
    }

    /**
     * Attempts to match the ArmorType for the specified ItemStack.
     *
     * @param itemStack The ItemStack to parse the type of.
     * @return The parsed ArmorType, or null if not found.
     */
    public static ArmorType matchType(final ItemStack itemStack) {
        if (ArmorChangeListener.isAirOrNull(itemStack)) return null;
        String type = itemStack.getType().name();
        if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD")) return HELMET;
        else if (type.endsWith("_CHESTPLATE") || type.equals("ELYTRA")) return CHESTPLATE;
        else if (type.endsWith("_LEGGINGS")) return LEGGINGS;
        else if (type.endsWith("_BOOTS")) return BOOTS;
        else return null;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }
}