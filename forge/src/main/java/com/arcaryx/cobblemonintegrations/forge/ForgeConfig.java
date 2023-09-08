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
    private final ForgeConfigSpec.BooleanValue hidePokemonLabel;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> pokemonTooltips;

    public ForgeConfig(ForgeConfigSpec.Builder builder) {
        builder.push("waila");
        builder.comment("You can disable specific modules in the WAILA config.");
        builder.push("pokemon");
        builder.comment("Valid TooltipTypes: " + String.join(", ", Arrays.stream(TooltipType.values()).map(Enum::name).toList()));
        pokemonTooltips = builder.defineList("pokemonTooltips", TooltipType.pokemonDefaults.stream().map(x -> x.getLeft().name() + ":" + x.getRight().name()).toList(), TooltipType::check);
        builder.pop().push("misc");
        hidePokemonLabel = builder
                .comment("Hide the Pokemon label/nametag completely.")
                .define("hidePokemonLabel", ModList.get().isLoaded("jade"));
    }

    @Override
    public boolean hidePokemonLabel() {
        return hidePokemonLabel.get();
    }

    @Override
    public List<Pair<TooltipType, ShowType>> getPokemonTooltips() {
        return pokemonTooltips.get().stream().map(x -> {
            var str = x.split(":");
            return Pair.of(TooltipType.valueOf(str[0]), ShowType.valueOf(str[1]));
        }).toList();
    }
}