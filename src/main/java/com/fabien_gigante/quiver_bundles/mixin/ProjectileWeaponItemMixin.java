package com.fabien_gigante.quiver_bundles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.quiver_bundles.BundleSlot;

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
    private static void useAmmoFromBundles(ItemStack weapon, ItemStack projectile, LivingEntity holder, boolean forceInfinite, CallbackInfoReturnable<ItemStack> cir) {
        BundleSlot slot = projectile.remove(BundleSlot.BUNDLE_SLOT);
        if (slot == null) return;
        BundleContents.Mutable contents = new BundleContents.Mutable(slot.bundle().get(DataComponents.BUNDLE_CONTENTS));
        contents.toggleSelectedItem(slot.index());
        contents.removeOne();
        if (!projectile.isEmpty()) contents.tryInsert(projectile);
        else projectile.setCount(1); // prevents checking player inventory
        slot.bundle().set(DataComponents.BUNDLE_CONTENTS, contents.toImmutable());
    }

    @Inject(method = "useAmmo", at = @At("RETURN"), cancellable = true)
    private static void cleanAmmoFromBundles(ItemStack weapon, ItemStack projectile, LivingEntity holder, boolean forceInfinite, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack ammo = cir.getReturnValue();
        projectile.remove(BundleSlot.BUNDLE_SLOT);
        ammo.remove(BundleSlot.BUNDLE_SLOT);
        cir.setReturnValue(ammo);
    }
}