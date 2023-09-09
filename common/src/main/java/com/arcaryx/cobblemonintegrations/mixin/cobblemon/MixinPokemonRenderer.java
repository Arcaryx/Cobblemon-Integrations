package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PokemonRenderer.class, remap = false)
abstract class MixinPokemonRenderer {

    @Redirect(method = "render(Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/client/render/pokemon/PokemonRenderer;shouldRenderLabel(Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;)Z"), require = 1)
    private boolean mixinShouldRenderLabel(PokemonRenderer instance, PokemonEntity entity) {
        if (CobblemonIntegrations.CONFIG.isModLoaded("jade") && CobblemonIntegrations.CONFIG.hidePokemonLabel()) {
            return false;
        }
        return shouldRenderLabel(entity);
    }

    @Shadow
    @Final
    private boolean shouldRenderLabel(PokemonEntity entity) {
        return false;
    }

}
