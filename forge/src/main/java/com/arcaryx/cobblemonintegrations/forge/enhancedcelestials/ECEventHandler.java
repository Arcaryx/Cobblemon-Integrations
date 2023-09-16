package com.arcaryx.cobblemonintegrations.forge.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.enhancedcelestials.PokemonLunarEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DataPackRegistryEvent;

public class ECEventHandler {
    @SubscribeEvent
    public static void registerDatapack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(PokemonLunarEvent.KEY, PokemonLunarEvent.CODEC);
    }
}
