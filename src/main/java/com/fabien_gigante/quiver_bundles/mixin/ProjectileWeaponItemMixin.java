package com.fabien_gigante.quiver_bundles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.quiver_bundles.QuiverHolder;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.core.component.DataComponents;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {
    @Inject(
        method = "useAmmo",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;",
            shift = At.Shift.AFTER
        )
    )
    private static void useAmmoFromQuiver(ItemStack weapon, ItemStack projectile, LivingEntity holder, boolean forceInfinite, CallbackInfoReturnable<ItemStack> cir) {
        QuiverHolder.QuiverSlot slot = holder instanceof QuiverHolder archer ? archer.getCurrentQuiverSlot(projectile) : null;
        BundleContents contents = slot != null ? slot.bundle().get(DataComponents.BUNDLE_CONTENTS) : null;
        if (contents == null) return;
        int index = contents.getSelectedItemIndex();
        BundleContents.Mutable newContents = contents.asMutable();
        if (index != slot.index()) newContents.toggleSelectedItem(slot.index());
        newContents.removeOne();
        if (!projectile.isEmpty()) newContents.tryInsert(projectile);
        else projectile.setCount(1); // prevents checking player inventory
        slot.bundle().set(DataComponents.BUNDLE_CONTENTS, newContents.toImmutable());
    }
}