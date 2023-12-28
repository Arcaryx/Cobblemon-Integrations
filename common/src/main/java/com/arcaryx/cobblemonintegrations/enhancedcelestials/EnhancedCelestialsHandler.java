package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.pokemon.IVs;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.api.EnhancedCelestialsRegistry;
import corgitaco.enhancedcelestials.api.lunarevent.DefaultLunarEvents;
import corgitaco.enhancedcelestials.api.lunarevent.LunarEvent;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import kotlin.math.MathKt;
import kotlin.random.Random;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.Map;

public class EnhancedCelestialsHandler {

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

    public static int ECModifyBattleExp(int baseExp, Level level) {
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

    public static void ECModifyBattleEVs(Map<Stat, Integer> baseChanges, Level level) {
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

    public static boolean IsOngoingLunarEvent(Level level, ResourceLocation lunarEvent) {
        EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)level).getLunarContext();
        return enhancedCelestialsContext != null && enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(lunarEvent);
    }

    public static Component GetLunarEventName(ResourceLocation eventLocation, Level level) {
        Registry<LunarEvent> lunarEventRegistry = level.registryAccess().registryOrThrow(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY);
        var lunarEvent = lunarEventRegistry.get(eventLocation);
        if (lunarEvent != null) {
            return lunarEvent.getTextComponents().name().getComponent();
        }
        return Component.literal(eventLocation.getPath());
    }


}
