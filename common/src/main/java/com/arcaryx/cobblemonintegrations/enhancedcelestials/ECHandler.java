package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.api.EnhancedCelestialsRegistry;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import kotlin.math.MathKt;
import kotlin.random.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ECHandler {

    public static void ECModifySpawns(PokemonProperties props, SpawningContext ctx) {
        var level = ctx.getWorld();
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
                        props.setShiny(true);
                    }
                }
                if (event.getPerfIVs() > 0) {
                    // TODO: Count perfect IVs if already provided, then only override if stats are better (mod compat)
                    props.setIvs(IVs.Companion.createRandomIVs(event.getPerfIVs()));
                }
                break;
            }
        }
    }

    public static int ECModifyBattleExp(PokemonEntity pokemonEntity, int baseExp) {
        Level level = ((Entity)pokemonEntity).level(); // Idk why this cast is necessary
        EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)level).getLunarContext();
        if (enhancedCelestialsContext != null) {
            var registry = level.registryAccess().registryOrThrow(PokemonLunarEvent.KEY);
            for (var event : registry) {
                if (!enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(event.getLunarEvent())) {
                    continue;
                }
                return MathKt.roundToInt(baseExp * event.getExpMultiplier());
            }
        }
        return baseExp;
    }

    public static void ECModifyBattleEVs(PokemonEntity pokemonEntity, Map<Stat, Integer> baseChanges) {
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
}
