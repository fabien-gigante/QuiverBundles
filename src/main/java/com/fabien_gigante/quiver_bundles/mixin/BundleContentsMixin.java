package com.fabien_gigante.quiver_bundles.mixin;

import java.math.RoundingMode;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.math.IntMath;
import com.mojang.serialization.DataResult;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.component.BundleContents;

@Mixin(BundleContents.class)
public abstract class BundleContentsMixin {
    @Shadow @Final @Mutable
    public static DataResult<Fraction> BEEHIVE_WEIGHT;

    @Inject(method="<clinit>", at=@At("TAIL"))
    private static void init(CallbackInfo ci) {
        BEEHIVE_WEIGHT = DataResult.success(Fraction.getFraction(1, 8));
    }

    @Redirect(method="getWeight", at=@At(value="INVOKE", target="Lnet/minecraft/world/item/ItemInstance;getMaxStackSize()I"))
    private static int getWeight(ItemInstance itemInstance) {
        return 8 * IntMath.sqrt(itemInstance.getMaxStackSize(), RoundingMode.FLOOR);
    }
}