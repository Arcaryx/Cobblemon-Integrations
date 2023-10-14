package com.arcaryx.cobblemonintegrations.fabric;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.fabric.enhancedcelestials.PokemonLunarEventLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CobblemonIntegrationsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonIntegrations.CONFIG = FabricConfig.load();
        CobblemonIntegrations.NETWORK = new FabricNetworkHandler();
        CobblemonIntegrations.init();

        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials")) {
            PokemonLunarEventLoader.init();
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            CobblemonIntegrations.NETWORK.registerMessagesClient();
        }

    }
}