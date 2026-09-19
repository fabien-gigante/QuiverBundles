package com.fabien_gigante.quiver_bundles.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO ? Doesn't produce optimal results : several partial stacks of the same can be left in the bundle
@Mixin(BundleContents.Mutable.class)
public abstract class BundleContentsMutableMixin {
    @Redirect(
        method = "findStackIndexWithinRange",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private boolean canMerge(ItemStack stack, ItemStack itemsToAdd) {
        return ItemStack.isSameItemSameComponents(stack, itemsToAdd)
            && itemsToAdd.getCount() <= stack.getMaxStackSize() - stack.getCount();
    }

    @ModifyVariable(method = "tryInsert", at = @At("STORE"), ordinal = 0)
    private int limitStackSize(int amountToAdd, ItemStack itemsToAdd) {
        return Math.min(amountToAdd, itemsToAdd.getMaxStackSize());
    }
}