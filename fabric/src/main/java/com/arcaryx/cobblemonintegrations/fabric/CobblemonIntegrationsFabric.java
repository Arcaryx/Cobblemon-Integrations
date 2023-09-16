package com.arcaryx.cobblemonintegrations.fabric;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.fabric.enhancedcelestials.PokemonLunarEventLoader;
import com.sun.jna.platform.win32.Winnetwk;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.List;

public class CobblemonIntegrationsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobblemonIntegrations.CONFIG = FabricConfig.load();
        CobblemonIntegrations.NETWORK = new FabricNetworkHandler();
        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials")) {
            PokemonLunarEventLoader.init();
        }
        CobblemonIntegrations.init();
    }
}