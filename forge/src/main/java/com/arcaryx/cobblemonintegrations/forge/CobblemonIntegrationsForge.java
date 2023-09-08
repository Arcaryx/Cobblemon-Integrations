package com.arcaryx.cobblemonintegrations.forge;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod(CobblemonIntegrations.MOD_ID)
public class CobblemonIntegrationsForge {

    private static final ForgeConfig config;
    private static final ForgeConfigSpec commonSpec;

    static {
        final Pair<ForgeConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(ForgeConfig::new);
        config = common.getLeft();
        commonSpec = common.getRight();
    }

    public CobblemonIntegrationsForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
        CobblemonIntegrations.CONFIG = config;
        CobblemonIntegrations.NETWORK = new ForgeNetworkHandler();
        CobblemonIntegrations.init();
    }
}