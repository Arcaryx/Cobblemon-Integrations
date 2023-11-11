package com.arcaryx.cobblemonintegrations.jei.evoitems;

import com.arcaryx.cobblemonintegrations.data.PokemonItemEvo;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.evolution.variants.ItemInteractionEvolution;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class EvoItemsWrapper implements IRecipeCategoryExtension {

    private final PokemonItemEvo itemEvo;

    private final List<Item> validItems;

    private PokemonFloatingState state;
    private long last;

    public EvoItemsWrapper(PokemonItemEvo itemEvo) {
        this.itemEvo = itemEvo;
        this.validItems = BuiltInRegistries.ITEM.stream().filter(x -> itemEvo.getItemEvo().getRequiredContext().getItem().fits(x, BuiltInRegistries.ITEM)).toList();;
    }

    public List<Item> getValidItems() {
        return validItems;
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics graphics, double mouseX, double mouseY) {
        if (state == null) {
            state = new PokemonFloatingState();
        }
        // Why am I being forced to compute partialTicks?
        var elapsed = System.currentTimeMillis() - last;
        last = System.currentTimeMillis();
        var partialTicks = Mth.clamp(elapsed / 100F, 0F, 1F);

        var pose = graphics.pose();

        pose.pushPose();
        var speciesBase = PokemonSpecies.INSTANCE.getByIdentifier(itemEvo.getSpecies());
        var formBase = speciesBase.getForms().stream().filter(x -> x.getName().equals(itemEvo.getForm())).findFirst().orElse(speciesBase.getStandardForm());
        var component = speciesBase.getTranslatedName();
        if (speciesBase.getStandardForm() != formBase && !formBase.getName().equalsIgnoreCase("base")) {
            component.append(Component.literal(String.format(" (%s)", formBase.getName())));
        }
        graphics.drawString(Minecraft.getInstance().font, component, 2, 1, Objects.requireNonNull(ChatFormatting.WHITE.getColor()));
        pose.popPose();


        var pokemonBase = new RenderablePokemon(speciesBase, new HashSet<>(formBase.getAspects()));
        var evoAspects = new HashSet<>(formBase.getAspects());
        var evoSpecies = PokemonSpecies.INSTANCE.getByName(itemEvo.getItemEvo().getResult().getSpecies());
        for (var prop : itemEvo.getItemEvo().getResult().getCustomProperties()) {
            if (prop instanceof FlagSpeciesFeature sf) {
                if (sf.getEnabled()) {
                    evoAspects.add(sf.getName());
                } else {
                    evoAspects.remove(sf.getName());
                }
            }
        }

        var pokemonEvo = new RenderablePokemon(evoSpecies, evoAspects);

        var m1 = pose.last().pose();
        var l1 = m1.m30();
        var t1 = m1.m31();
        var rotationY = -30F;

        // Pokemon Base
        pose.pushPose();
        graphics.enableScissor((int)l1 + 2, (int)t1 + 13, (int)l1 + 61, (int)t1 + 92);
        pose.translate(31, 13, 0);
        pose.scale(1F, 1F, 1F);
        pose.pushPose();
        PokemonGuiUtilsKt.drawProfilePokemon(
                pokemonBase,
                pose,
                QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, rotationY, 0F)),
                state,
                partialTicks,
                40F
        );
        pose.popPose();
        graphics.disableScissor();
        pose.popPose();

        // Pokemon Evo
        pose.pushPose();
        graphics.enableScissor((int)l1 + 91, (int)t1 + 13, (int)l1 + 150, (int)t1 + 92);
        pose.translate(120, 13, 0);
        pose.scale(1F, 1F, 1F);
        pose.pushPose();
        PokemonGuiUtilsKt.drawProfilePokemon(
                pokemonEvo,
                pose,
                QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13F, rotationY, 0F)),
                state,
                partialTicks,
                40F
        );
        pose.popPose();
        graphics.disableScissor();
        pose.popPose();
    }
}
