package com.arcaryx.cobblemonintegrations.jei;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.data.ClientCache;
import com.arcaryx.cobblemonintegrations.jei.drops.DropsJeiCategory;
import com.arcaryx.cobblemonintegrations.jei.drops.DropsWrapper;
import com.arcaryx.cobblemonintegrations.jei.evoitems.EvoItemsJeiCategory;
import com.arcaryx.cobblemonintegrations.jei.evoitems.EvoItemsWrapper;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JeiPlugin
public class CobblemonJeiPlugin implements IModPlugin {
    public static final ResourceLocation DROPS = new ResourceLocation(CobblemonIntegrations.MOD_ID, "cobblemon_drops");
    public static final RecipeType<DropsWrapper> DROPS_TYPE = new RecipeType<>(DROPS, DropsWrapper.class);
    public static final ResourceLocation EVO_ITEMS = new ResourceLocation(CobblemonIntegrations.MOD_ID, "cobblemon_evoitems");
    public static final RecipeType<EvoItemsWrapper> EVO_ITEMS_TYPE = new RecipeType<>(EVO_ITEMS, EvoItemsWrapper.class);

    private static IJeiHelpers jeiHelpers;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(CobblemonIntegrations.MOD_ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Pokemon Drops
        List<DropsWrapper> drops = new ArrayList<>();
        for (var species : PokemonSpecies.INSTANCE.getSpecies()) {
            var baseTexture = PokemonModelRepository.INSTANCE.getTexture(species.getResourceIdentifier(), Set.copyOf(species.getStandardForm().getAspects()), 0);
            var forms = species.getForms().isEmpty() ? List.of(species.getStandardForm()) : species.getForms();
            for (var form : forms) {
                var formTexture = PokemonModelRepository.INSTANCE.getTexture(species.getResourceIdentifier(), Set.copyOf(form.getAspects()), 0);
                var isSubstitute = formTexture.getPath().contains("substitute");
                var isBaseForm = form.getName().equals(species.getStandardForm().getName());
                var hasNewTexture = baseTexture != formTexture;
                var hasNewDrops = !ClientCache.sameDrops(species, form, species, species.getStandardForm());
                if (isSubstitute || (!isBaseForm && !hasNewTexture && !hasNewDrops))
                    continue;
                drops.add(new DropsWrapper(species, form));
            }
        }
        registration.addRecipes(DROPS_TYPE, drops);
        // Evolution Items
        List<EvoItemsWrapper> evoItems = new ArrayList<>();
        for (var itemEvo : ClientCache.getPokemonItemEvos()) {
            var speciesBase = PokemonSpecies.INSTANCE.getByIdentifier(itemEvo.getSpecies());
            if (itemEvo.getItemEvo().getResult().getSpecies() == null) {
                CobblemonIntegrations.LOGGER.warn("Null evolution result from species (1): " + itemEvo.getSpecies());
                continue;
            }
            var speciesEvo = PokemonSpecies.INSTANCE.getByName(itemEvo.getItemEvo().getResult().getSpecies());
            if (speciesBase == null || speciesEvo == null) {
                CobblemonIntegrations.LOGGER.warn("Null evolution result from species (2): " + itemEvo.getSpecies());
                continue;
            }

            //var baseTexture = PokemonModelRepository.INSTANCE.getTexture(itemEvo.getSpecies(), new HashSet<>(), 0);
            //var isStandardFormBase = itemEvo.getForm().equals(speciesBase.getStandardForm().getName()) || itemEvo.getForm().equals("base");
            //var aspects = new HashSet<String>();
            //aspects.add(itemEvo.getForm().toLowerCase());
            //var formTexture = PokemonModelRepository.INSTANCE.getTexture(itemEvo.getSpecies(), aspects, 0);
            // // Fuck Pikachu, WHY DID THEY DO THIS?

            //if (!isStandardFormBase && (baseTexture == formTexture)) {
            //    continue;
            //}

            evoItems.add(new EvoItemsWrapper(itemEvo));
           //var speciesBase = PokemonSpecies.INSTANCE.getByIdentifier(itemEvo.getSpecies());
           //
           //
           //
           //
           //
           //var speciesBase = itemEvo.getSpeciesBase();
           //var formBase = itemEvo.getFormBase();
           //// isStandardFormBase indicates if the pre-evo is the standard form for the species TODO: This had a check for "base" for some reason, verify this isn't needed
           //var isStandardFormBase = formBase.getName().equals(speciesBase.getStandardForm().getName());
           //var standardTextureBase = PokemonModelRepository.INSTANCE.getTexture(speciesBase.getResourceIdentifier(), Set.copyOf(speciesBase.getStandardForm().getAspects()), 0);
           //// This takes into account any features
           //var formTextureBase = PokemonModelRepository.INSTANCE.getTexture(speciesBase.getResourceIdentifier(), Set.copyOf(itemEvo.getAspectsBase()), 0);
           //// hasNewTextureBase indicates if the pre-evo has a custom texture
           //var hasNewTextureBase = standardTextureBase != formTextureBase;
           //// isSubstituteBase indicates if hte pre-evo has an unimplemented substitute texture
           //var isSubstituteBase = formTextureBase.getPath().contains("substitute");

           //var speciesEvo = itemEvo.getSpeciesEvo();
           //var formEvo = itemEvo.getFormEvo();
           //// isStandardFormBase indicates if the evo is the standard form for the species
           //var isStandardFormEvo = formEvo.getName().equals(speciesEvo.getStandardForm().getName());
           //var standardTextureEvo = PokemonModelRepository.INSTANCE.getTexture(speciesEvo.getResourceIdentifier(), Set.copyOf(speciesEvo.getStandardForm().getAspects()), 0);
           //// This takes into account any features
           //var formTextureEvo = PokemonModelRepository.INSTANCE.getTexture(speciesEvo.getResourceIdentifier(), Set.copyOf(itemEvo.getAspectsEvo()), 0);
           //// hasNewTextureEvo indicates if the evo has a custom texture
           //var hasNewTextureEvo = standardTextureEvo != formTextureEvo;
           //// isSubstituteEvo indicates if hte evo has an unimplemented substitute texture
           //var isSubstituteEvo = formTextureEvo.getPath().contains("substitute");

           //if (isSubstituteBase || isSubstituteEvo) {
           //    continue;
           //}

           //if (!isStandardFormBase && !hasNewTextureBase) {
           //    continue;
           //}

           //if (!isStandardFormEvo && !hasNewTextureEvo) {
           //    continue;
           //}

            //evoItems.add(new EvoItemsWrapper(speciesBase, formBase, new HashSet<>(itemEvo.getAspectsBase()), itemEvo.getSpeciesEvo(), itemEvo.getFormEvo(), new HashSet<>(itemEvo.getAspectsEvo()), itemEvo.getValidItems()));

        }
        registration.addRecipes(EVO_ITEMS_TYPE, evoItems);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        CobblemonJeiPlugin.jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(
            new DropsJeiCategory(),
            new EvoItemsJeiCategory()
        );
    }

    public static IJeiHelpers getJeiHelpers() {
        return jeiHelpers;
    }


}
