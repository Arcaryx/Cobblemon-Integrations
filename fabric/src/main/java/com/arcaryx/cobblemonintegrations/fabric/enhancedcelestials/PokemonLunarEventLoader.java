package com.arcaryx.cobblemonintegrations.fabric.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.PokemonLunarEvent;
import corgitaco.enhancedcelestials.EnhancedCelestials;
import corgitaco.enhancedcelestials.mixin.RegistryDataLoaderAccess;
import corgitaco.enhancedcelestials.mixin.RegistrySynchronizationAccess;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PokemonLunarEventLoader {

    public static void init() {
        PokemonLunarEvent.KEY = ResourceKey.createRegistryKey(new ResourceLocation(CobblemonIntegrations.MOD_ID,  CobblemonIntegrations.MOD_ID + "/" + EnhancedCelestials.MOD_ID));

        List<RegistryDataLoader.RegistryData<?>> registryData = new ArrayList<>(RegistryDataLoader.WORLDGEN_REGISTRIES);
        registryData.add(new RegistryDataLoader.RegistryData<>(PokemonLunarEvent.KEY, PokemonLunarEvent.CODEC));

        RegistryDataLoaderAccess.ec_setWORLDGEN_REGISTRIES(registryData);
        Reference2ObjectMap<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> map = new Reference2ObjectOpenHashMap<>(RegistrySynchronizationAccess.ec_getNETWORKABLE_REGISTRIES());

        map.put(PokemonLunarEvent.KEY, new RegistrySynchronization.NetworkedRegistryData<>(PokemonLunarEvent.KEY, PokemonLunarEvent.CODEC));
        RegistrySynchronizationAccess.ec_setNETWORKABLE_REGISTRIES(map);
    }
}
