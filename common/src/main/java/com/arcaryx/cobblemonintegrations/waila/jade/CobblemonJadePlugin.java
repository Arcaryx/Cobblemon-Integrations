package com.arcaryx.cobblemonintegrations.waila.jade;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.block.*;
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(CobblemonIntegrations.MOD_ID)
public class CobblemonJadePlugin implements IWailaPlugin {
    public static final ResourceLocation POKEMON_ENTITY = new ResourceLocation(CobblemonIntegrations.MOD_ID, "pokemon_entity");
    public static final ResourceLocation HEALER = new ResourceLocation(CobblemonIntegrations.MOD_ID, "healer");
    public static final ResourceLocation APRICORN = new ResourceLocation(CobblemonIntegrations.MOD_ID, "apricorn");
    public static final ResourceLocation BERRY_BUSH = new ResourceLocation(CobblemonIntegrations.MOD_ID, "berry_bush");
    public static final ResourceLocation FOSSIL_ANALYZER = new ResourceLocation(CobblemonIntegrations.MOD_ID, "fossil_analyzer");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(PokemonProvider.INSTANCE, PokemonEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(PokemonProvider.INSTANCE, PokemonEntity.class);
        registration.registerBlockComponent(HealerProvider.INSTANCE, HealingMachineBlock.class);
        registration.registerBlockComponent(ApricornProvider.INSTANCE, ApricornBlock.class);
        registration.registerBlockComponent(BerryProvider.INSTANCE, BerryBlock.class);
        registration.registerBlockComponent(FossilAnalyzerProvider.INSTANCE, FossilAnalyzerBlock.class);
        registration.registerBlockComponent(FossilAnalyzerProvider.INSTANCE, RestorationTankBlock.class);
        registration.registerBlockComponent(FossilAnalyzerProvider.INSTANCE, MonitorBlock.class);
    }
}
