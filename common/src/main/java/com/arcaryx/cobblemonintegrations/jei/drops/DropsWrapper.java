package com.arcaryx.cobblemonintegrations.jei.drops;

import com.arcaryx.cobblemonintegrations.data.ClientCache;
import com.arcaryx.cobblemonintegrations.data.PokemonDrop;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class DropsWrapper implements IRecipeCategoryExtension, IRecipeSlotTooltipCallback {
    private final Species species;
    private final FormData form;
    private PokemonFloatingState state;
    private long last;

    public DropsWrapper(Species species, FormData form) {
        this.species = species;
        this.form = form;
    }

    public List<PokemonDrop> getDrops() {
        return ClientCache.getPokemonDrops(species, form);
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, GuiGraphics graphics, double mouseX, double mouseY) {
        if (state == null) {
            state = new PokemonFloatingState();
        }
        // Why am I being forced to compute partialTicks?
        var elapsed = System.currentTimeMillis() - last;
        last = System.currentTimeMillis();
        var partialTicks = Mth.clamp(elapsed / 50F, 0F, 1F);

        var pose = graphics.pose();

        pose.pushPose();
        var component = species.getTranslatedName();
        if (species.getStandardForm() != form) {
            component.append(Component.literal(String.format(" (%s)", form.getName())));
        }
        graphics.drawString(Minecraft.getInstance().font, component, 2, 1, Objects.requireNonNull(ChatFormatting.WHITE.getColor()));
        pose.popPose();



        var pokemon = new RenderablePokemon(species, new HashSet<>(form.getAspects()));

        var m1 = pose.last().pose();
        var l1 = m1.m30();
        var t1 = m1.m31();

        pose.pushPose();

        graphics.enableScissor((int)l1 + 2, (int)t1 + 13, (int)l1 + 61, (int)t1 + 92);

        var rotationY = -30F;
        pose.translate(31, 13, 0);
        pose.scale(1F, 1F, 1F);
        pose.pushPose();
        PokemonGuiUtilsKt.drawProfilePokemon(
                pokemon,
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


    @Override
    public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
        var drop = getDrops().get(Integer.parseInt(recipeSlotView.getSlotName().orElse("0")));
        var component = Component.literal(String.valueOf(drop.getRange().getFirst()));
        var chance = drop.getChance() * 100;
        var chanceString = chance < 10 ? String.format("%.1f", chance) : String.format("%2d", (int)chance);
        if (drop.getRange().getFirst() != drop.getRange().getLast()) {
            component.append("-" + drop.getRange().getLast());
        }
        component.append((drop.getChance() < 1F ? " (" + chanceString + "%)" : ""));
        tooltip.add(component);
    }
}
