package com.arcaryx.cobblemonintegrations.forge.tan;

import com.arcaryx.cobblemonintegrations.forge.CobblemonIntegrationsForge;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.FormData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import toughasnails.api.temperature.IPlayerTemperatureModifier;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

import java.util.*;

public class PokemonTemperatureModifier implements IPlayerTemperatureModifier {

    private record PokemonTemp(boolean wild, boolean owned, double dist, FormData form, int level) {
    }

    public static void registerModifier() {
        TemperatureHelper.registerPlayerTemperatureModifier(new PokemonTemperatureModifier());
    }

    @Override
    public TemperatureLevel modify(Player player, TemperatureLevel temperatureLevel) {
        if (!CobblemonIntegrationsForge.CONFIG.pokemonAffectsTemperature.get()) {
            return temperatureLevel;
        }

        var range = CobblemonIntegrationsForge.CONFIG.temperatureRange.get() + CobblemonIntegrationsForge.CONFIG.temperatureRangePerLevel.get() * 255;
        var nearbyPokemon = player.level().getEntitiesOfClass(PokemonEntity.class, player.getBoundingBox().inflate(range));
        var nearbyPlayers = player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(range));
        var pokemonList = new ArrayList<>(nearbyPokemon.stream().map(x -> new PokemonTemp(x.getPokemon().isWild(), x.isOwnedBy(player), player.distanceToSqr(x), x.getForm(), x.getPokemon().getLevel())).toList());
        for (var x : nearbyPlayers) {
            if (!x.getShoulderEntityLeft().isEmpty() && x.getShoulderEntityLeft().contains("Pokemon")) {
                var pokemonTag = x.getShoulderEntityLeft().getCompound("Pokemon");
                addShoulderPokemon(pokemonList, x.is(player), x.distanceToSqr(player), pokemonTag);
            }
            if (!x.getShoulderEntityRight().isEmpty() && x.getShoulderEntityRight().contains("Pokemon")) {
                var pokemonTag = x.getShoulderEntityRight().getCompound("Pokemon");
                addShoulderPokemon(pokemonList, x.is(player), x.distanceToSqr(player), pokemonTag);
            }
        }
        if (CobblemonIntegrationsForge.CONFIG.temperatureRangePerLevel.get() > 0) {
            pokemonList.removeIf(x -> CobblemonIntegrationsForge.CONFIG.temperatureRange.get() + CobblemonIntegrationsForge.CONFIG.temperatureRangePerLevel.get() * x.level > x.dist);
        }
        if (!CobblemonIntegrationsForge.CONFIG.wildAffectsTemperature.get()) {
            pokemonList.removeIf(x -> x.wild);
        }
        if (!CobblemonIntegrationsForge.CONFIG.capturedAffectsTemperature.get()) {
            pokemonList.removeIf(x -> !x.wild);
        }
        pokemonList.sort((Comparator.comparingDouble((PokemonTemp pokemon) -> pokemon.owned ? Double.MAX_VALUE : pokemon.dist)));

        double newTemp = temperatureLevel.ordinal() - 2;
        for (var pokemon : pokemonList) {

            var primaryTemp = CobblemonIntegrationsForge.CONFIG.temperatureStrength.get() + CobblemonIntegrationsForge.CONFIG.temperatureStrengthPerLevel.get() * pokemon.level;
            var secondaryTemp = CobblemonIntegrationsForge.CONFIG.secondaryTypeHalfStrength.get() ?  primaryTemp * 0.5 : primaryTemp;

            double deltaTemp = 0;
            if (Objects.equals(pokemon.form.getPrimaryType(), ElementalTypes.INSTANCE.getFIRE()))
                deltaTemp += primaryTemp;
            else if (Objects.equals(pokemon.form.getSecondaryType(), ElementalTypes.INSTANCE.getFIRE()))
                deltaTemp += secondaryTemp;
            if (Objects.equals(pokemon.form.getPrimaryType(), ElementalTypes.INSTANCE.getICE()))
                deltaTemp -= primaryTemp;
            else if (Objects.equals(pokemon.form.getSecondaryType(), ElementalTypes.INSTANCE.getICE()))
                deltaTemp -= secondaryTemp;

            if ((pokemon.wild && CobblemonIntegrationsForge.CONFIG.wildCausesHarm.get()) ||
                    (pokemon.owned && CobblemonIntegrationsForge.CONFIG.capturedCausesHarm.get()) ||
                    (!pokemon.wild && !pokemon.owned && CobblemonIntegrationsForge.CONFIG.capturedCausesHarmOther.get())) {
                newTemp = Mth.clamp(newTemp + deltaTemp, -2, 2);
            } else {
                if ((newTemp >= 1.999 && deltaTemp > 0) || (newTemp <= -1.999 && deltaTemp < 0)) {
                    deltaTemp = 0;
                }
                else {
                    deltaTemp = Mth.clamp(deltaTemp, Math.max(-1.0 - newTemp, -2.0), Math.min(1.0 - newTemp, 2.0));
                }
                newTemp = newTemp + deltaTemp;
            }
        }

        return TemperatureLevel.values()[(int)Math.round(newTemp) + 2];
    }

    private void addShoulderPokemon(ArrayList<PokemonTemp> pokemonList, boolean owned, double dist, CompoundTag pokemonTag) {
        var species = new ResourceLocation(pokemonTag.getString("Species"));
        var formTag = pokemonTag.getString("FormId");
        var level = pokemonTag.getShort("Level");
        var formData = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByIdentifier(species)).getForm(Collections.singleton(formTag));
        pokemonList.add(new PokemonTemp(false, owned, dist, formData, level));
    }
}