package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.EnhancedCelestialsHandler;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceCalculator;
import com.cobblemon.mod.common.api.pokemon.stats.EvCalculator;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(value = PokemonBattle.class, remap = false)
abstract class MixinPokemonBattle {

    @Redirect(method = "end", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/pokemon/experience/ExperienceCalculator;calculate(Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;D)I"))
    private int modifyBattleExperience(ExperienceCalculator instance, BattlePokemon opponentPokemon, BattlePokemon faintedPokemon, double multiplier) {
        var baseExp = instance.calculate(opponentPokemon, faintedPokemon, multiplier);
        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials") &&
                opponentPokemon.getOriginalPokemon().isPlayerOwned() &&
                (CobblemonIntegrations.CONFIG.applyInPVP() || faintedPokemon.getOriginalPokemon().isWild())) {
            baseExp = EnhancedCelestialsHandler.ECModifyBattleExp(baseExp, opponentPokemon.getOriginalPokemon().getOwnerPlayer().level());
        }
        return baseExp;
    }

    @Redirect(method = "end", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/pokemon/stats/EvCalculator;calculate(Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;Lcom/cobblemon/mod/common/battles/pokemon/BattlePokemon;)Ljava/util/Map;"))
    private Map<Stat, Integer> modifyBattleEVs(EvCalculator instance, BattlePokemon opponentPokemon, BattlePokemon faintedPokemon) {
        var baseChanges = instance.calculate(opponentPokemon, faintedPokemon);
        if (CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials") &&
                opponentPokemon.getOriginalPokemon().isPlayerOwned() &&
                (CobblemonIntegrations.CONFIG.applyInPVP() || faintedPokemon.getOriginalPokemon().isWild())) {
            EnhancedCelestialsHandler.ECModifyBattleEVs(baseChanges, opponentPokemon.getOriginalPokemon().getOwnerPlayer().level());
        }
        return baseChanges;
    }

}
