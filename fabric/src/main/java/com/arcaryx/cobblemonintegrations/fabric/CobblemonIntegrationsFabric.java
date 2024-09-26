package com.arcaryx.cobblemonintegrations.fabric;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.fabric.enhancedcelestials.PokemonLunarEventLoader;
import com.arcaryx.cobblemonintegrations.fabric.waila.jade.JadeFabricUtil;
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

        if (CobblemonIntegrations.CONFIG.isModLoaded("jade")) {
            CobblemonIntegrations.JADE_UTIL = new JadeFabricUtil();
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            CobblemonIntegrations.NETWORK.registerMessagesClient();
        }

    }
}