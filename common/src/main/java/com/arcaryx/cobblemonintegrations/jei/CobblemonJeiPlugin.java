package com.arcaryx.cobblemonintegrations.jei;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.data.ClientCache;
import com.arcaryx.cobblemonintegrations.jei.drops.DropsJeiCategory;
import com.arcaryx.cobblemonintegrations.jei.drops.DropsWrapper;
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
import java.util.List;
import java.util.Set;

@JeiPlugin
public class CobblemonJeiPlugin implements IModPlugin {
    public static final ResourceLocation DROPS = new ResourceLocation(CobblemonIntegrations.MOD_ID, "cobblemon_drops");
    public static final RecipeType<DropsWrapper> DROPS_TYPE = new RecipeType<>(DROPS, DropsWrapper.class);

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
            var baseTexture = PokemonModelRepository.INSTANCE.getTexture(species.getResourceIdentifier(), Set.copyOf(species.getStandardForm().getAspects()), null);
            var forms = species.getForms().isEmpty() ? List.of(species.getStandardForm()) : species.getForms();
            for (var form : forms) {
                var formTexture = PokemonModelRepository.INSTANCE.getTexture(species.getResourceIdentifier(), Set.copyOf(form.getAspects()), null);
                var isBaseForm = form.getName().equals(species.getStandardForm().getName());
                var hasNewTexture = baseTexture != formTexture;
                var hasNewDrops = !ClientCache.sameDrops(species, form, species, species.getStandardForm());
                if (!isBaseForm && !hasNewTexture && !hasNewDrops)
                    continue;
                drops.add(new DropsWrapper(species, form));
            }
        }
        registration.addRecipes(DROPS_TYPE, drops);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        CobblemonJeiPlugin.jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(
            new DropsJeiCategory()
        );
    }

    public static IJeiHelpers getJeiHelpers() {
        return jeiHelpers;
    }


}
