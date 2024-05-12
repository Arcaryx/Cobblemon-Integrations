package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LunarEventCondition implements AppendageCondition {
    public ResourceLocation lunarEvent;

    public static void register() {
        // TODO: AppendageCondition.Companion.registerAppendage(AreaSpawningCondition.class, LunarEventCondition.class);
        AppendageCondition.Companion.registerAppendage(LunarEventCondition.class, x -> true);
    }

    @Override
    public boolean fits(@NotNull SpawningContext spawningContext) {
        if (lunarEvent == null) {
            return true;
        }
        if (!CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials") || !CobblemonIntegrations.CONFIG.allowLunarEventVariants()) {
            return false;
        }
        return EnhancedCelestialsHandler.IsOngoingLunarEvent(spawningContext.getWorld(), lunarEvent);
    }
}
