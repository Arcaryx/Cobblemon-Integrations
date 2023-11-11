package com.arcaryx.cobblemonintegrations.data;

import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import net.minecraft.resources.ResourceLocation;

public class PokemonItemEvo {
    private final ResourceLocation species;
    private final String form;
    private final ItemInteractionEvolution itemEvo;

    public PokemonItemEvo(ResourceLocation species, String form, ItemInteractionEvolution itemEvo) {
        this.species = species;
        this.form = form;
        this.itemEvo = itemEvo;
    }

    public ResourceLocation getSpecies() {
        return species;
    }

    public String getForm() {
        return form;
    }

    public ItemInteractionEvolution getItemEvo() {
        return itemEvo;
    }
}
