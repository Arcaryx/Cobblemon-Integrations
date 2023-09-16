package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.enhancedcelestials.EnhancedCelestials;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PokemonLunarEvent {

    public static final ResourceKey<Registry<PokemonLunarEvent>> KEY = ResourceKey.createRegistryKey(new ResourceLocation(CobblemonIntegrations.MOD_ID, EnhancedCelestials.MOD_ID));

    public static final Codec<PokemonLunarEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("lunar_event").forGetter(PokemonLunarEvent::getLunarEvent),
            Codec.FLOAT.fieldOf("shiny_multiplier").forGetter(PokemonLunarEvent::getShinyMultiplier),
            Codec.INT.fieldOf("min_perf_ivs").forGetter(PokemonLunarEvent::getPerfIVs),
            Codec.FLOAT.fieldOf("ev_multiplier").forGetter(PokemonLunarEvent::getEVMultiplier),
            Codec.FLOAT.fieldOf("aggression_multiplier").forGetter(PokemonLunarEvent::getAggressionMultiplier)
    ).apply(instance, PokemonLunarEvent::new));

    private final ResourceLocation lunarEvent;
    private final float shinyMultiplier, evMultiplier, aggressionMultiplier;
    private final int perfIVs;

    public PokemonLunarEvent(ResourceLocation lunarEvent, float shinyMultiplier, int perfIvs, float evMultiplier, float aggressionMultiplier) {
        this.lunarEvent = lunarEvent;
        this.shinyMultiplier = shinyMultiplier;
        this.perfIVs = perfIvs;
        this.evMultiplier = evMultiplier;
        this.aggressionMultiplier = aggressionMultiplier;
    }

    public ResourceLocation getLunarEvent() {
        return lunarEvent;
    }

    public float getShinyMultiplier() {
        return shinyMultiplier;
    }

    public int getPerfIVs() {
        return perfIVs;
    }

    public float getEVMultiplier() {
        return evMultiplier;
    }

    public float getAggressionMultiplier() {
        return aggressionMultiplier;
    }
}
