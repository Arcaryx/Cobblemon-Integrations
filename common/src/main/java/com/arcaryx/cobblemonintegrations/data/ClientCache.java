package com.arcaryx.cobblemonintegrations.data;

import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ClientCache {
    private static final Map<Pair<ResourceLocation, String>, List<PokemonDrop>> pokemonDrops = new HashMap<>();
    private static final List<PokemonItemEvo> pokemonItemEvos = new ArrayList<>();

    public static void setPokemonDrops(List<PokemonDrop> drops) {
        pokemonDrops.clear();
        for (var drop : drops) {
            var pokemon = Pair.of(drop.getSpecies(), drop.getForm());
            if (!pokemonDrops.containsKey(pokemon))
                pokemonDrops.put(pokemon, new ArrayList<>());
            pokemonDrops.get(pokemon).add(drop);
        }
    }

    public static List<PokemonDrop> getPokemonDrops(Species species, FormData form) {
        var pokemon = Pair.of(species.getResourceIdentifier(), form.getName());
        if (pokemonDrops.containsKey(pokemon))
            return pokemonDrops.get(pokemon);
        return new ArrayList<>();
    }

    public static boolean sameDrops(Species species1, FormData form1, Species species2, FormData form2) {
        var pokemon1 = Pair.of(species1.getResourceIdentifier(), form1.getName());
        var pokemon2 = Pair.of(species2.getResourceIdentifier(), form2.getName());
        var drops1 = pokemonDrops.getOrDefault(pokemon1, new ArrayList<>());
        var drops2 = pokemonDrops.getOrDefault(pokemon2, new ArrayList<>());
        if (drops1.size() != drops2.size())
            return false;
        Collections.sort(drops1);
        Collections.sort(drops2);
        return drops1.equals(drops2);
    }

    public static void setPokemonItemEvos(List<PokemonItemEvo> evos) {
        pokemonItemEvos.clear();
        pokemonItemEvos.addAll(evos);
    }

    public static List<PokemonItemEvo> getPokemonItemEvos() {
        return pokemonItemEvos;
    }
}
