package com.arcaryx.cobblemonintegrations.forge.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.PokemonLunarEvent;
import corgitaco.enhancedcelestials.EnhancedCelestials;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DataPackRegistryEvent;

public class ECEventHandler {
    @SubscribeEvent
    public static void registerDatapack(DataPackRegistryEvent.NewRegistry event) {
        PokemonLunarEvent.KEY = ResourceKey.createRegistryKey(new ResourceLocation(CobblemonIntegrations.MOD_ID, EnhancedCelestials.MOD_ID));
        event.dataPackRegistry(PokemonLunarEvent.KEY, PokemonLunarEvent.CODEC);
    }
}
