package com.arcaryx.cobblemonintegrations.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PokemonUtils {

    public static CompoundTag saveStatMapToCompoundTag(Map<Stat, Integer> statMap) {
        Map<ResourceLocation, Integer> resourceLocationMap = new HashMap<>();
        for (Map.Entry<Stat, Integer> entry : statMap.entrySet()) {
            resourceLocationMap.put(entry.getKey().getIdentifier(), entry.getValue());
        }

        CompoundTag compoundTag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Integer> entry : resourceLocationMap.entrySet()) {
            compoundTag.putInt(entry.getKey().toString(), entry.getValue());
        }

        return compoundTag;
    }

    public static Map<Stat, Integer> loadStatMapFromCompoundTag(CompoundTag compoundTag) {
        Map<ResourceLocation, Integer> resourceLocationMap = new HashMap<>();
        for (String key : compoundTag.getAllKeys()) {
            ResourceLocation resourceLocation = new ResourceLocation(key);
            int value = compoundTag.getInt(key);
            resourceLocationMap.put(resourceLocation, value);
        }

        Map<Stat, Integer> statMap = new HashMap<>();
        Arrays.stream(Stats.values()).forEach(stat -> {
            ResourceLocation identifier = stat.getIdentifier();
            if (resourceLocationMap.containsKey(identifier)) {
                statMap.put(stat, resourceLocationMap.get(identifier));
            }
        });

        return statMap;
    }

    public static boolean hasHiddenAbility(Pokemon pokemon) {
        var abilities = pokemon.getForm().getAbilities();
        for (var ability : abilities) {
            if (ability instanceof HiddenAbility hiddenAbility && hiddenAbility.getTemplate() == pokemon.getAbility().getTemplate()) {
                return true;
            }
        }
        return false;
    }

    public static Gender getGenderFromShowdownName(String showdownName) {
        for (Gender gender : Gender.values()) {
            if (gender.getShowdownName().equals(showdownName)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid showdownName: " + showdownName);
    }
}
