package com.arcaryx.cobblemonintegrations.data;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public class PokemonItemEvo {
    public final ResourceLocation speciesBase;
    public final String formBase;
    public final ResourceLocation speciesEvo;
    public final String formEvo;
    public final List<ResourceLocation> validItems;

    public PokemonItemEvo(ResourceLocation speciesBase, String formBase, ResourceLocation speciesEvo, String formEvo, List<ResourceLocation> validItems) {
        this.speciesBase = speciesBase;
        this.formBase = formBase;
        this.speciesEvo = speciesEvo;
        this.formEvo = formEvo;
        this.validItems = validItems;
    }

    public Species getSpeciesBase() {
        return PokemonSpecies.INSTANCE.getByIdentifier(speciesBase);
    }

    public FormData getFormBase() {
        var species = getSpeciesBase();
        var speciesForms = species.getForms().isEmpty() ? List.of(species.getStandardForm()) : species.getForms();
        return speciesForms.stream().filter(x -> x.getName().equalsIgnoreCase(formBase)).findFirst().get();
    }

    public Species getSpeciesEvo() {
        return PokemonSpecies.INSTANCE.getByIdentifier(speciesEvo);
    }

    public FormData getFormEvo() {
        var species = getSpeciesEvo();
        var speciesForms = species.getForms().isEmpty() ? List.of(species.getStandardForm()) : species.getForms();
        return speciesForms.stream().filter(x -> x.getName().equalsIgnoreCase(formEvo)).findFirst().get();
    }

    public List<Item> getValidItems() {
        return validItems.stream().map(x -> BuiltInRegistries.ITEM.get(x).asItem()).toList();
    }
}
