package com.arcaryx.cobblemonintegrations;

import com.cobblemon.mod.common.api.spawning.condition.AppendageCondition;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NeededInstalledModsFix implements AppendageCondition {

    public List<String> neededInstalledMods;

    public static void register() {
        AppendageCondition.Companion.registerAppendage(NeededInstalledModsFix.class, x -> true);
    }

    @Override
    public boolean fits(@NotNull SpawningContext spawningContext, @NotNull SpawnDetail spawnDetail) {
        if (neededInstalledMods == null) {
            return true;
        }
        for (var mod : neededInstalledMods) {
            if (!CobblemonIntegrations.CONFIG.isModLoaded(mod)) {
                return false;
            }
        }
        return true;
    }
}
