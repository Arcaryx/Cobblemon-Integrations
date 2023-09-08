package com.arcaryx.cobblemonintegrations.waila.jade;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.config.ShowType;
import com.cobblemon.mod.common.block.ApricornBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ApricornProvider implements IBlockComponentProvider {
    INSTANCE;

    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        Block block = state.getBlock();

        if (!(block instanceof ApricornBlock)) {
            return;
        }

        addMaturityTooltip(tooltip, (float)state.getValue(ApricornBlock.Companion.getAGE()) / ApricornBlock.MAX_AGE);
    }

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F) {
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", String.format("%.0f%%", growthValue)));
        } else {
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", Component.translatable("tooltip.jade.crop_mature").withStyle(ChatFormatting.GREEN)));
        }
    }

    public ResourceLocation getUid() {
        return CobblemonJadePlugin.APRICORN;
    }
}