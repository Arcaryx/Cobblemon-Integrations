package com.arcaryx.cobblemonintegrations.fabric;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import net.fabricmc.api.ClientModInitializer;

public class CobblemonIntegrationsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CobblemonIntegrations.initClient();
    }
}
