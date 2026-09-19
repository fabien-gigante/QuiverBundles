package com.fabien_gigante.quiver_bundles.mixin;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fabien_gigante.quiver_bundles.QuiverHolder;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BundleContents;

@Mixin(Player.class)
public abstract class PlayerMixin implements QuiverHolder {
	@Shadow
	public abstract Inventory getInventory();
	@Unique
	private @Nullable QuiverSlot currentQuiverSlot;

    public @Override QuiverSlot getCurrentQuiverSlot() { return currentQuiverSlot; }
    public @Override void setCurrentQuiverSlot(QuiverSlot slot) { this.currentQuiverSlot = slot; }

	@Inject(method = "getProjectile", at = @At(value = "HEAD"), cancellable = true)
	private void clearQuiverProjectile(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir) {
		clearCurrentQuiverSlot();
	}

    @Inject(
        method = "getProjectile",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z"),
        cancellable = true
    )
	private void getQuiverProjectile(ItemStack weapon, CallbackInfoReturnable<ItemStack> cir) {
		Predicate<ItemStack> supportedProjectiles = ((ProjectileWeaponItem)weapon.getItem()).getAllSupportedProjectiles();
		for (ItemStack stack : getInventory()) {
			if (stack.isEmpty()) continue;
			BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
			if (bundle == null) continue;
			List<ItemStackTemplate> items = bundle.items();
			for (int j = 0; j < items.size(); j++) {
				ItemStackTemplate inside = items.get(j);
				if (!inside.is(ItemTags.ARROWS)) continue;
				ItemStack projectile = inside.create();
				if (!supportedProjectiles.test(projectile)) continue;
				setCurrentQuiverSlot(projectile, stack, j);
				cir.setReturnValue(projectile);
				return;
			}
		}
	}

}