package com.arcaryx.cobblemonintegrations.waila.jade;

import com.arcaryx.cobblemonintegrations.util.TextUtils;
import com.cobblemon.mod.common.block.BerryBlock;
import com.cobblemon.mod.common.block.entity.BerryBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum BerryProvider implements IBlockComponentProvider {
    INSTANCE;

    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        Block block = state.getBlock();

        if (!(block instanceof BerryBlock)) {
            return;
        }

        addMaturityTooltip(tooltip, (float)state.getValue(BerryBlock.Companion.getAGE()) / BerryBlock.FRUIT_AGE);
        addBerryTooltip(tooltip, (BerryBlockEntity)accessor.getBlockEntity());
    }

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F) {
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", String.format("%.0f%%", growthValue)));
        } else {
            tooltip.add(Component.translatable("tooltip.jade.crop_growth", Component.translatable("tooltip.jade.crop_mature").withStyle(ChatFormatting.GREEN)));
        }
    }

    private static void addBerryTooltip(ITooltip tooltip, BerryBlockEntity treeEntity) {
        var berry = treeEntity.berry();
        if (berry != null) {
            String berryTooltip = I18n.get("item.cobblemon." + berry.getIdentifier().getPath() + ".tooltip");
            var berryLines = TextUtils.wrapString(berryTooltip, 32);
            for (var line : berryLines) {
                tooltip.add(Component.literal(line));
            }
        }
    }


    public ResourceLocation getUid() {
        return CobblemonJadePlugin.BERRY_BUSH;
    }
}
