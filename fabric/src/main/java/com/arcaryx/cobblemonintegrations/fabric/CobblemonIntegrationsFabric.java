package com.arcaryx.cobblemonintegrations.fabric;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.fabric.enhancedcelestials.PokemonLunarEventLoader;
import net.fabricmc.api.ModInitializer;

public class CobblemonIntegrationsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonIntegrations.CONFIG = FabricConfig.load();
        CobblemonIntegrations.NETWORK = new FabricNetworkHandler();
        CobblemonIntegrations.init();

        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials")) {
            PokemonLunarEventLoader.init();
        }

    }
}