package com.arcaryx.cobblemonintegrations.jei;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BlankJeiCategory<T extends IRecipeCategoryExtension> implements IRecipeCategory<T> {
    private final String name;
    private final IDrawable icon, gui;

    protected BlankJeiCategory(String name, IDrawable icon, IDrawable gui) {
        this.name = name;
        this.icon = icon;
        this.gui = gui;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei." + name + ".title");
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return gui;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        recipe.drawInfo(getBackground().getWidth(), getBackground().getHeight(), guiGraphics, mouseX, mouseY);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        recipe.getTooltip(tooltip, mouseX, mouseY);
    }
}
