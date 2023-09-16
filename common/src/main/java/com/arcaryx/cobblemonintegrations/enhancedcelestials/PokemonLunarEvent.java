package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PokemonLunarEvent {

    public static ResourceKey<Registry<PokemonLunarEvent>> KEY;

    public static final Codec<PokemonLunarEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("lunar_event").forGetter(PokemonLunarEvent::getLunarEvent),
            Codec.FLOAT.fieldOf("shiny_multiplier").forGetter(PokemonLunarEvent::getShinyMultiplier),
            Codec.INT.fieldOf("min_perf_ivs").forGetter(PokemonLunarEvent::getPerfIVs),
            Codec.FLOAT.fieldOf("exp_multiplier").forGetter(PokemonLunarEvent::getExpMultiplier),
            Codec.FLOAT.fieldOf("ev_multiplier").forGetter(PokemonLunarEvent::getEVMultiplier)
            //Codec.FLOAT.fieldOf("aggression_multiplier").forGetter(PokemonLunarEvent::getAggressionMultiplier)
    ).apply(instance, PokemonLunarEvent::new));

    private final ResourceLocation lunarEvent;
    private final float shinyMultiplier, expMultiplier, evMultiplier;
    private final int perfIVs;

    public PokemonLunarEvent(ResourceLocation lunarEvent, float shinyMultiplier, int perfIvs, float expMultiplier, float evMultiplier) {
        this.lunarEvent = lunarEvent;
        this.shinyMultiplier = shinyMultiplier;
        this.perfIVs = perfIvs;
        this.expMultiplier = expMultiplier;
        this.evMultiplier = evMultiplier;
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

    public float getExpMultiplier() {
        return expMultiplier;
    }

    public float getEVMultiplier() {
        return evMultiplier;
    }
}
