package com.arcaryx.cobblemonintegrations.waystones;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.Objects;
import java.util.stream.StreamSupport;

public class WaystonesUtils {

    public static boolean CanUseTeleport(Pokemon pokemon) {
        var hasTeleportLearned = pokemon.getMoveSet().getMoves().stream().anyMatch((x) -> x.getName().equals("teleport"));
        var hasTeleportBenched = StreamSupport.stream(pokemon.getBenchedMoves().spliterator(), false)
                .anyMatch((x) -> x.getMoveTemplate().getName().equals("teleport"));
        var hasTeleportAccessible = pokemon.getAllAccessibleMoves().stream().anyMatch((x) -> x.getName().equals("teleport"));
        var hasTeleport = hasTeleportLearned || hasTeleportBenched || hasTeleportAccessible;
        var isPsychicType = Objects.equals(pokemon.getForm().getPrimaryType(), ElementalTypes.INSTANCE.getPSYCHIC()) ||
                Objects.equals(pokemon.getForm().getSecondaryType(), ElementalTypes.INSTANCE.getPSYCHIC());
        if (pokemon.getLevel() < CobblemonIntegrations.CONFIG.waystoneMinTeleportLevel()) {
            return false;
        }
        return hasTeleport || (!CobblemonIntegrations.CONFIG.requireTeleportMove() && isPsychicType);
    }
}
