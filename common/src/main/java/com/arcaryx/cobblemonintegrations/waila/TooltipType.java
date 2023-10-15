package com.arcaryx.cobblemonintegrations.waila;

import com.arcaryx.cobblemonintegrations.config.ShowType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public enum TooltipType {
    TITLE,
    TITLE_GENDER,
    SPECIES,
    LEVEL,
    GENDER,
    HEALTH,
    TRAINER,
    NICKNAME,
    FRIENDSHIP,
    TYPES,
    REWARD_EVS,
    NATURE,
    ABILITY,
    IVS,
    EVS,
    DEX_ENTRY,
    SNEAK_HINT,
    BATTLE_HINT;


    public static boolean check(Object entry) {
        if (!(entry instanceof String entryStr)) {
            return false;
        }
        var str = entryStr.split(":");
        if (str.length != 2) {
            return false;
        }
        if (Arrays.stream(TooltipType.values()).noneMatch(x -> str[0].equals(x.name()))) {
            return false;
        }
        return Arrays.stream(ShowType.values()).anyMatch(x -> str[1].equals(x.name()));
    }

    public static final List<Pair<TooltipType, ShowType>> pokemonDefaults = Arrays.asList(
            Pair.of(TooltipType.TITLE_GENDER, ShowType.SHOW),
            Pair.of(TooltipType.HEALTH, ShowType.SHOW),
            Pair.of(TooltipType.TRAINER, ShowType.SHOW),
            Pair.of(TooltipType.TYPES, ShowType.SHOW),
            Pair.of(TooltipType.REWARD_EVS, ShowType.SHOW),
            Pair.of(TooltipType.SNEAK_HINT, ShowType.NO_SNEAK),
            Pair.of(TooltipType.FRIENDSHIP, ShowType.SNEAK),
            Pair.of(TooltipType.NATURE, ShowType.SNEAK),
            Pair.of(TooltipType.ABILITY, ShowType.SNEAK),
            Pair.of(TooltipType.IVS, ShowType.SNEAK),
            Pair.of(TooltipType.EVS, ShowType.SNEAK),
            Pair.of(TooltipType.DEX_ENTRY, ShowType.SNEAK),
            Pair.of(TooltipType.BATTLE_HINT, ShowType.SNEAK)
    );
}
