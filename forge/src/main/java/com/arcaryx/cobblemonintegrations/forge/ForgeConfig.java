package com.arcaryx.cobblemonintegrations.forge;

import com.arcaryx.cobblemonintegrations.config.IConfig;
import com.arcaryx.cobblemonintegrations.config.ShowType;
import com.arcaryx.cobblemonintegrations.waila.TooltipType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class ForgeConfig implements IConfig {

    // WAILA
    private ForgeConfigSpec.BooleanValue hidePokemonLabel;
    private ForgeConfigSpec.ConfigValue<List<? extends String>> pokemonTooltips;

    // TaN
    public ForgeConfigSpec.BooleanValue pokemonAffectsTemperature, wildAffectsTemperature, capturedAffectsTemperature,
            wildCausesHarm, capturedCausesHarm, capturedCausesHarmOther, secondaryTypeHalfStrength, pokemonGiveWater,
            requirePrimaryType, fillBottle, fillCanteen;
    public ForgeConfigSpec.IntValue minLevelForDirty, minLevelForNormal, minLevelForPurified;
    public ForgeConfigSpec.DoubleValue temperatureRange, temperatureRangePerLevel, temperatureStrength, temperatureStrengthPerLevel;

    public ForgeConfig(ForgeConfigSpec.Builder builder) {
        if (ModList.get().isLoaded("jade")) {
            builder.push("waila");
            builder.comment("You can disable specific modules in the WAILA config.");
            builder.push("pokemon");
            builder.comment("Valid TooltipTypes: " + String.join(", ", Arrays.stream(TooltipType.values()).map(Enum::name).toList()));
            pokemonTooltips = builder.defineList("pokemonTooltips", TooltipType.pokemonDefaults.stream().map(x -> x.getLeft().name() + ":" + x.getRight().name()).toList(), TooltipType::check);
            builder.pop().push("misc");
            hidePokemonLabel = builder
                    .comment("Hide the Pokemon label/nametag completely.")
                    .define("hidePokemonLabel", true);
            builder.pop().pop();
        }
        if (ModList.get().isLoaded("toughasnails")) {
            builder.push("toughasnails").push("temperature");
            pokemonAffectsTemperature = builder
                    .comment("Should any Pokemon affect the player's temperature.")
                    .define("pokemonAffectsTemperature", true);
            wildAffectsTemperature = builder
                    .comment("Should wild Pokemon affect the player's temperature.")
                    .define("wildAffectsTemperature", true);
            wildCausesHarm = builder
                    .comment("Should wild Pokemon be able to put players into temperature extremes.")
                    .define("wildCausesHarm", true);
            capturedAffectsTemperature = builder
                    .comment("Should captured Pokemon affect the player's temperature.")
                    .define("capturedAffectsTemperature", true);
            capturedCausesHarm = builder
                    .comment("Should captured Pokemon be able to put their owner into temperature extremes.")
                    .define("capturedCausesHarm", false);
            capturedCausesHarmOther = builder
                    .comment("Should captured Pokemon be able to put other players into temperature extremes.")
                    .define("capturedCausesHarmOther", false);
            builder.push("range");
            temperatureRange = builder
                    .comment("Base range for Pokemon to affect the player's temperature.")
                    .defineInRange("temperatureRange", 5.0, 0.0, 255.0);
            temperatureRangePerLevel = builder
                    .comment("Range increase per level for Pokemon to affect the player's temperature.")
                    .defineInRange("temperatureRangePerLevel", 0.0, 0.0, 255.0);
            builder.pop().push("strength");
            secondaryTypeHalfStrength = builder
                    .comment("Should the Pokemon's secondary type have half-effectiveness.")
                    .define("secondaryTypeHalfStrength", true);
            temperatureStrength = builder
                    .comment("Base strength for Pokemon to affect the player's temperature.")
                    .defineInRange("temperatureStrength", 2.0, 0.0, 4.0);
            temperatureStrengthPerLevel = builder
                    .comment("Strength increase per level for Pokemon to affect the player's temperature.")
                    .defineInRange("temperatureStrengthPerLevel", 0.0, 0.0, 1.0);
            builder.pop().pop().push("hydration");
            pokemonGiveWater = builder
                    .comment("Should any Pokemon give the player water.")
                    .define("pokemonGiveWater", true);
            requirePrimaryType = builder
                    .comment("Should Pokemon need to have Water as their primary type to give water.")
                    .define("requirePrimaryType", false);
            fillBottle = builder
                    .comment("Can Pokemon fill bottles.")
                    .define("fillBottle", true);
            fillCanteen = builder
                    .comment("Can Pokemon fill canteens.")
                    .define("fillCanteen", true);
            builder.push("level");
            minLevelForDirty = builder
                    .comment("Minimum level for dirty water.")
                    .defineInRange("minLevelForDirty", 0, 0, 255);
            minLevelForNormal = builder
                    .comment("Minimum level for normal water.")
                    .defineInRange("minLevelForNormal", 25, 0, 255);
            minLevelForPurified = builder
                    .comment("Minimum level for purified water.")
                    .defineInRange("minLevelForPurified", 50, 0, 255);
            builder.pop().pop().pop();
        }
    }

    @Override
    public boolean hidePokemonLabel() {
        if (hidePokemonLabel != null) {
            return hidePokemonLabel.get();
        }
        return true;
    }

    @Override
    public List<Pair<TooltipType, ShowType>> getPokemonTooltips() {
        if (pokemonTooltips != null) {
            return pokemonTooltips.get().stream().map(x -> {
                var str = x.split(":");
                return Pair.of(TooltipType.valueOf(str[0]), ShowType.valueOf(str[1]));
            }).toList();
        }
        return TooltipType.pokemonDefaults;
    }
}