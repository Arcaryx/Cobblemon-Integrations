package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.PokemonLunarEvent;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceCalculator;
import com.cobblemon.mod.common.api.pokemon.stats.EvCalculator;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import kotlin.math.MathKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(value = PokemonBattle.class, remap = false)
abstract class MixinPokemonBattle {

    @Redirect(method = "end", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/pokemon/experience/ExperienceCalculator;calculate(Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;D)I"))
    private int modifyBattleExperience(ExperienceCalculator instance, BattlePokemon opponentPokemon, BattlePokemon faintedPokemon, double multiplier) {
        var pokemonEntity = opponentPokemon.getEntity();
        var faintedPokemonEntity = faintedPokemon.getEntity();
        var baseExp = instance.calculate(opponentPokemon, faintedPokemon, multiplier);
        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials") &&
                pokemonEntity.getPokemon().isPlayerOwned() &&
                (CobblemonIntegrations.CONFIG.applyInPVP() || faintedPokemonEntity.getPokemon().isWild())) {
            Level level = ((Entity)pokemonEntity).level(); // Idk why this cast is necessary
            EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)level).getLunarContext();
            if (enhancedCelestialsContext != null) {
                var registry = level.registryAccess().registryOrThrow(PokemonLunarEvent.KEY);
                for (var event : registry) {
                    if (!enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(event.getLunarEvent())) {
                        continue;
                    }
                    baseExp = MathKt.roundToInt(baseExp * event.getExpMultiplier());
                    break;
                }
            }
        }
        return baseExp;
    }

    @Redirect(method = "end", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/pokemon/stats/EvCalculator;calculate(Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;)Ljava/util/Map;"))
    private Map<Stat, Integer> modifyBattleEVs(EvCalculator instance, BattlePokemon opponentPokemon, BattlePokemon faintedPokemon) {
        var pokemonEntity = opponentPokemon.getEntity();
        var faintedPokemonEntity = faintedPokemon.getEntity();
        var baseChanges = instance.calculate(opponentPokemon, faintedPokemon);
        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials") &&
                pokemonEntity.getPokemon().isPlayerOwned() &&
                (CobblemonIntegrations.CONFIG.applyInPVP() || faintedPokemonEntity.getPokemon().isWild())) {
            Level level = ((Entity)pokemonEntity).level(); // Idk why this cast is necessary
            EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)level).getLunarContext();
            if (enhancedCelestialsContext != null) {
                var registry = level.registryAccess().registryOrThrow(PokemonLunarEvent.KEY);
                for (var event : registry) {
                    if (!enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(event.getLunarEvent())) {
                        continue;
                    }
                    for (var stat : baseChanges.keySet()) {
                        int base = baseChanges.get(stat);
                        baseChanges.put(stat, MathKt.roundToInt(Mth.ceil(base * event.getEVMultiplier())));
                    }
                    break;
                }
            }
        }
        return baseChanges;
    }


}
