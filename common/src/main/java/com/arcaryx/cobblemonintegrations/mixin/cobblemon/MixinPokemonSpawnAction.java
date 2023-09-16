package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.PokemonLunarEvent;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import kotlin.random.Random;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.cobblemon.mod.common.pokemon.IVs;

@Mixin(value = PokemonSpawnAction.class, remap = false)
abstract class MixinPokemonSpawnAction extends SpawnAction<PokemonEntity> {

    public MixinPokemonSpawnAction(@NotNull SpawningContext ctx, @NotNull SpawnDetail detail) {
        super(ctx, detail);
    }

    @Inject(method = "createEntity()Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;", at = @At("HEAD"))
    private void adjustCreateEntity(CallbackInfoReturnable<PokemonEntity> cir) {
        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials")) {
            var level = getCtx().getWorld();
            EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)level).getLunarContext();
            if (enhancedCelestialsContext != null) {
                var registry = level.registryAccess().registryOrThrow(PokemonLunarEvent.KEY);
                for (var event : registry) {
                    if (!enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(event.getLunarEvent())) {
                        continue;
                    }
                    if (Cobblemon.config.getShinyRate() >= 1f && event.getShinyMultiplier() > 0f) {
                        var modifiedShinyRate = Cobblemon.config.getShinyRate() / event.getShinyMultiplier();
                        if (Random.Default.nextFloat() < 1f / modifiedShinyRate) {
                            this.props.setShiny(true);
                        }
                    }
                    if (event.getPerfIVs() > 0) {
                        // TODO: Count perfect IVs if already provided, then only override if stats are better (mod compat)
                        this.props.setIvs(IVs.Companion.createRandomIVs(event.getPerfIVs()));
                    }
                    break;
                }
            }
        }
    }

    @Shadow
    private PokemonProperties props;





}
