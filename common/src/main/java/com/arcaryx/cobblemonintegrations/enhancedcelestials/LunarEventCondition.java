package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LunarEventCondition implements AppendageCondition {
    public ResourceLocation lunarEvent;

    public static void register() {
        //AppendageCondition.Companion.registerAppendage(AreaSpawningCondition.class, LunarEventCondition.class);
        AppendageCondition.Companion.registerAppendage(LunarEventCondition.class, x -> true);
    }

    @Override
    public boolean fits(@NotNull SpawningContext spawningContext, @NotNull SpawnDetail spawnDetail) {
        if (lunarEvent == null) {
            return true;
        }
        if (!CobblemonIntegrations.CONFIG.allowLunarEventVariants()) {
            return false;
        }
        EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)spawningContext.getWorld()).getLunarContext();
        return enhancedCelestialsContext != null && enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(this.lunarEvent);
    }
}
