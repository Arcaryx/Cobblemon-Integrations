package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.cobblemon.mod.common.block.entity.FossilAnalyzerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FossilAnalyzerBlockEntity.FossilAnalyzerInventory.class)
abstract class MixinFossilAnalyzerInventory {
//    @Inject(method = "getContainerSize", at = @At("HEAD"), cancellable = true)
//    private void fixGetContainerSize(CallbackInfoReturnable<Integer> cir) {
//        cir.setReturnValue(1);
//    }

}
