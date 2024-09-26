package com.arcaryx.cobblemonintegrations.forge;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.forge.enhancedcelestials.ECEventHandler;
import com.arcaryx.cobblemonintegrations.forge.tan.TaNEventHandler;
import com.arcaryx.cobblemonintegrations.forge.waila.jade.JadeForgeUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        CobblemonIntegrations.NETWORK.registerMessagesClient();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (ModList.get().isLoaded("toughasnails")) {
            MinecraftForge.EVENT_BUS.register(TaNEventHandler.class);
        }
        if (ModList.get().isLoaded("enhancedcelestials")) {
            modBus.register(ECEventHandler.class);
        }
        if (ModList.get().isLoaded("jade")) {
            CobblemonIntegrations.JADE_UTIL = new JadeForgeUtil();
        }
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CobblemonIntegrations::initClient);
    }
}