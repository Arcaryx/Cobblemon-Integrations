package com.arcaryx.cobblemonintegrations.enhancedcelestials;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement;
import corgitaco.enhancedcelestials.EnhancedCelestialsWorldData;
import corgitaco.enhancedcelestials.core.EnhancedCelestialsContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class LunarEventRequirement implements EntityQueryRequirement {

    public ResourceLocation lunarEvent;

    public static final String ADAPTER_VARIANT = "lunar_event";

    @Override
    public boolean check(@NotNull Pokemon pokemon) {
        // TODO: This can be avoided if I use Kotlin smh
        LivingEntity queriedEntity = pokemon.getEntity();
        if (queriedEntity == null) {
            queriedEntity = pokemon.getOwnerPlayer();
            if (queriedEntity == null) {
                return false;
            }
        }
        return this.check(pokemon, queriedEntity);
    }

    @Override
    public boolean check(@NotNull Pokemon pokemon, @NotNull LivingEntity queriedEntity) {
        if (!CobblemonIntegrations.CONFIG.allowLunarEventVariants()) {
            return false;
        }
        EnhancedCelestialsContext enhancedCelestialsContext = ((EnhancedCelestialsWorldData)queriedEntity.level()).getLunarContext();
        return enhancedCelestialsContext != null && enhancedCelestialsContext.getLunarForecast().getCurrentEventRaw().is(this.lunarEvent);
    }
}
