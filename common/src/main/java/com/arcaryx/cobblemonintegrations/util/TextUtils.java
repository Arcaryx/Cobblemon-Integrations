package com.arcaryx.cobblemonintegrations.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.PokemonStats;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TextUtils {

    private static final Component[] STATS_SHORT = new Component[] {
            LocalizationUtilsKt.lang("ui.stats.hp"),
            LocalizationUtilsKt.lang("ui.stats.atk"),
            LocalizationUtilsKt.lang("ui.stats.def"),
            LocalizationUtilsKt.lang("ui.stats.sp_atk"),
            LocalizationUtilsKt.lang("ui.stats.sp_def"),
            LocalizationUtilsKt.lang("ui.stats.speed")
    };

    public static String basicPluralize(String input, int count){
        return "%s%s".formatted(input, count > 1 ? "s" : "");
    }

    public static List<String> wrapString(String input, int maxLength) {
        List<String> wrappedLines = new ArrayList<>();
        String[] words = input.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLength) {
                wrappedLines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        if (currentLine.length() > 0) {
            wrappedLines.add(currentLine.toString());
        }

        return wrappedLines;
    }

    public static String formatStats(PokemonStats pokemonStats, double maxTotal) {
        var baseStats = new Stat[6];
        System.arraycopy(Stats.values(), 0, baseStats, 0, 6);
        var stats = Arrays.stream(baseStats).mapToInt(pokemonStats::getOrDefault).toArray();
        var total = Arrays.stream(stats).sum();
        double percentage = (total / maxTotal) * 100;
        int flooredPercentage = (int)Math.floor(percentage);
        return String.format("%s (%d%%)", String.join("/", Arrays.stream(stats).mapToObj(Integer::toString).toList()), flooredPercentage);
    }

    public static Component formatEvYield(Map<Stat, Integer> pokemonStats) {
        var baseStats = Stats.values();
        var result = Component.empty();
        for (var i = 0; i < 6; i++) {
            var stat = baseStats[i];
            if (!pokemonStats.containsKey(stat) || pokemonStats.get(stat) == 0)
                continue;
            if (result.getSiblings().size() > 0)
                result.append(Component.literal(", "));
            result.append(Component.literal("%d ".formatted(pokemonStats.get(stat)))).append(STATS_SHORT[i]);
        }
        return result;
    }
}