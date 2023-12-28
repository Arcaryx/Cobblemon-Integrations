package com.arcaryx.cobblemonintegrations.data;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PokemonItemEvo {

    private String json;
    private final ResourceLocation species;
    private final String form;
    private ItemInteractionEvolution itemEvo;
    private final List<String> result;

    public PokemonItemEvo(ResourceLocation species, String form, String json, List<String> result) {
        this.json = json;
        this.species = species;
        this.form = form;
        this.result = result;
    }

    public PokemonItemEvo(ResourceLocation species, String form, ItemInteractionEvolution itemEvo, List<String> result) {
        this.json = null;
        this.species = species;
        this.form = form;
        this.itemEvo = itemEvo;
        this.result = result;
    }

    public ResourceLocation getSpecies() {
        return species;
    }

    public String getForm() {
        return form;
    }

    public ItemInteractionEvolution getItemEvo() {
        if (json != null) {
            load();
        }
        return itemEvo;
    }

    private void load() {
        var gson = PokemonSpecies.INSTANCE.getGson();
        itemEvo = gson.fromJson(json, ItemInteractionEvolution.class);
        json = null;
    }

    public List<String> getResult() {
        return result;
    }
}
