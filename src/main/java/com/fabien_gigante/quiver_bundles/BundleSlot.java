package com.fabien_gigante.quiver_bundles;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public record BundleSlot(ItemStack bundle, int index) {
    private static final BundleSlot EMPTY = new BundleSlot(ItemStack.EMPTY, -1);
    public static final DataComponentType<BundleSlot> BUNDLE_SLOT = DataComponentType.<BundleSlot>builder().persistent(MapCodec.unitCodec(EMPTY)).build();
}