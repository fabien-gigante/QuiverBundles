package com.fabien_gigante.quiver_bundles;

import net.minecraft.world.item.ItemStack;

public interface QuiverHolder {
    record QuiverSlot(ItemStack projectile, ItemStack bundle, int index) {}

    QuiverSlot getCurrentQuiverSlot();
    void setCurrentQuiverSlot(QuiverSlot slot);

    default void setCurrentQuiverSlot(ItemStack projectile, ItemStack bundle, int index) { 
        setCurrentQuiverSlot(new QuiverSlot(projectile, bundle, index)); 
    }
    default void clearCurrentQuiverSlot() { setCurrentQuiverSlot(null); }
    default QuiverSlot getCurrentQuiverSlot(ItemStack projectile) {
        QuiverSlot slot = getCurrentQuiverSlot();
        if (slot == null || !slot.projectile().equals(projectile)) return null;
        return slot;
    }
}