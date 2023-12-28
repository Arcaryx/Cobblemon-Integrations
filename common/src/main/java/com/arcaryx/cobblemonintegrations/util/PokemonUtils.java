package com.arcaryx.cobblemonintegrations.util;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.data.PokemonDrop;
import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import com.arcaryx.cobblemonintegrations.enhancedcelestials.LunarEventRequirement;
import com.cobblemon.mod.common.api.drop.ItemDropEntry;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import kotlin.ranges.IntRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

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

    public static List<PokemonItemEvo> computeItemEvos() {
        List<PokemonItemEvo> itemEvos = new ArrayList<>();
        for (var species : PokemonSpecies.INSTANCE.getSpecies()) {
            if (!species.getImplemented()) {
                continue;
            }

            var forms = species.getForms().isEmpty() ? List.of(species.getStandardForm()) : species.getForms();
            for (var form : forms) {
                for (var evolution : form.getEvolutions()) {
                    if (evolution instanceof ItemInteractionEvolution itemEvolution) {
                        var reqs = itemEvolution.getRequirements();
                        if (reqs.stream().anyMatch(x -> x instanceof LunarEventRequirement &&
                                (!CobblemonIntegrations.CONFIG.isModLoaded("enhancedcelestials") ||
                                        !CobblemonIntegrations.CONFIG.allowLunarEventVariants()))) {
                            continue;
                        }
                        var result = itemEvolution.getResult();
                        result.updateAspects();
                        var stripped = new ItemInteractionEvolution(
                                itemEvolution.getId(),
                                itemEvolution.getResult(),
                                itemEvolution.getRequiredContext(),
                                itemEvolution.getOptional(),
                                itemEvolution.getConsumeHeldItem(),
                                reqs,
                                itemEvolution.getLearnableMoves()
                        );

                        itemEvos.add(new PokemonItemEvo(species.getResourceIdentifier(), form.getName(), stripped, result.getAspects().stream().toList()));
                    }
                }
            }
        }
        return itemEvos;
    }

    private static void addDrops(List<PokemonDrop> lootDrops, Species species, FormData form) {
        var formDrops = form.getDrops().getEntries();
        for (var drop : formDrops) {
            if (!(drop instanceof ItemDropEntry itemDrop))
                continue;
            var range = itemDrop.getQuantityRange();
            if (range == null)
                range = new IntRange(itemDrop.getQuantity(), itemDrop.getQuantity());
            var chance = itemDrop.getPercentage() / 100;
            lootDrops.add(new PokemonDrop(species.getResourceIdentifier(), form.getName(), itemDrop.getItem(), chance, range.getFirst(), range.getLast()));
        }
    }

    public static List<PokemonDrop> computeLootDrops() {
        List<PokemonDrop> lootDrops = new ArrayList<>();
        var pokemonSpecies = PokemonSpecies.INSTANCE.getSpecies();
        for (var species : pokemonSpecies) {
            var forms = species.getForms();
            if (forms.isEmpty()) {
                var form = species.getStandardForm();
                addDrops(lootDrops, species, form);
                continue;
            }
            for (var form : species.getForms()) {
                addDrops(lootDrops, species, form);
            }
        }
        return lootDrops;
    }
}
