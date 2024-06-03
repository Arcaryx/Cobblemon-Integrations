package com.arcaryx.cobblemonintegrations.waila.jade;

import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity;
import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public enum FossilAnalyzerProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof FossilMultiblockEntity block)) {
            return;
        }
        if (!(block.getMultiblockStructure() instanceof FossilMultiblockStructure multiBlock)) {
tooltip.add(Component.literal("Structure Incomplete"));
            return;
        }

        if (multiBlock.getHasCreatedPokemon() && multiBlock.getResultingFossil() != null) {
            tooltip.add(Component.literal("Completed: " + I18n.get(multiBlock.getResultingFossil().getResult().create().getDisplayName().getString())).withStyle(ChatFormatting.GREEN));
            return;
        }

        if (multiBlock.getFossilInventory().isEmpty()) {
            tooltip.add(Component.literal("Fossil: None"));
        } else {
            var stack = multiBlock.getFossilInventory().get(0);
            tooltip.add(Component.literal("Fossil: " + I18n.get(stack.getItem().getName(stack).getString())));
        }

        var currentMat = (float)multiBlock.getOrganicMaterialInside();
        var maxMat = FossilMultiblockStructure.MATERIAL_TO_START;
        var percentageMat = currentMat / maxMat;
        IElementHelper helper = IElementHelper.get();
        var progressStyleMat = helper.progressStyle().color(0xFF000066, 0xFF00695C);
        var componentMat = Component.literal("Organic Material: %d%%".formatted(Math.round(percentageMat * 100))).withStyle(ChatFormatting.GRAY);

        //noinspection UnstableApiUsage
    tooltip.add(helper.progress(percentageMat, componentMat, progressStyleMat, BoxStyle.DEFAULT, true));

        if (multiBlock.isRunning()) {
            var current = (float)multiBlock.getTimeRemaining();
            var max = FossilMultiblockStructure.TIME_TO_TAKE;
            var percentage = 1f - (current / max);
            var progressStyle = helper.progressStyle().color(0xFF000066, 0xFF0000AA);
            var component = Component.literal("Progress: %d%%".formatted(Math.round(percentage * 100))).withStyle(ChatFormatting.GRAY);

            //noinspection UnstableApiUsage
            tooltip.add(helper.progress(percentage, component, progressStyle, BoxStyle.DEFAULT, true));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return CobblemonJadePlugin.FOSSIL_ANALYZER;
    }
}
