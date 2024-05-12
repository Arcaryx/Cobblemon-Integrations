package com.arcaryx.cobblemonintegrations.fabric.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.PokemonLunarEvent;
import corgitaco.enhancedcelestials.EnhancedCelestials;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PokemonLunarEventLoader {

    public static void init() {
        PokemonLunarEvent.KEY = ResourceKey.createRegistryKey(new ResourceLocation(CobblemonIntegrations.MOD_ID,  CobblemonIntegrations.MOD_ID + "/" + EnhancedCelestials.MOD_ID));
        DynamicRegistries.registerSynced(PokemonLunarEvent.KEY, PokemonLunarEvent.CODEC);
    }
}
