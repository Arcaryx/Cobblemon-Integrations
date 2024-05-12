package com.arcaryx.cobblemonintegrations.waila.jade;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.config.ShowType;
import com.cobblemon.mod.common.block.entity.HealingMachineBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public enum HealerProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof HealingMachineBlockEntity healer)) {
            return;
        }

        var current = healer.getHealingCharge();
        var max = healer.getMaxCharge();
        var percentage = current / max;
        IElementHelper helper = IElementHelper.get();
        var progressStyle = helper.progressStyle().color(0xFF000066, 0xFF0000AA);
        var component = Component.literal("Charge: %d%%".formatted(Math.round(percentage * 100))).withStyle(ChatFormatting.GRAY);

        //noinspection UnstableApiUsage
        tooltip.add(helper.progress(percentage, component, progressStyle, BoxStyle.DEFAULT, true));
    }

    @Override
    public ResourceLocation getUid() {
        return CobblemonJadePlugin.HEALER;
    }
}