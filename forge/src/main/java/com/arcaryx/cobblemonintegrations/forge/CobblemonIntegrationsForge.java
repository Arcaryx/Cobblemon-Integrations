package com.arcaryx.cobblemonintegrations.forge;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.forge.tan.PokemonTemperatureModifier;
import com.arcaryx.cobblemonintegrations.forge.tan.TaNEventHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod(CobblemonIntegrations.MOD_ID)
public class CobblemonIntegrationsForge {

    public static final ForgeConfig CONFIG;
    private static final ForgeConfigSpec commonSpec;

    static {
        final Pair<ForgeConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(ForgeConfig::new);
        CONFIG = common.getLeft();
        commonSpec = common.getRight();
    }

    public CobblemonIntegrationsForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
        CobblemonIntegrations.CONFIG = CONFIG;
        CobblemonIntegrations.NETWORK = new ForgeNetworkHandler();
        CobblemonIntegrations.init();
        MinecraftForge.EVENT_BUS.register(this);
        if (ModList.get().isLoaded("toughasnails")) {
            MinecraftForge.EVENT_BUS.register(TaNEventHandler.class);
        }

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        if(ModList.get().isLoaded("toughasnails")) {
            PokemonTemperatureModifier.registerModifier();
        }
    }
}