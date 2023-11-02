package com.arcaryx.cobblemonintegrations.jei.evoitems;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.jei.BlankJeiCategory;
import com.arcaryx.cobblemonintegrations.jei.CobblemonJeiPlugin;
import com.cobblemon.mod.common.CobblemonItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class EvoItemsJeiCategory extends BlankJeiCategory<EvoItemsWrapper> {
    public static final ResourceLocation GUI_LOCATION = new ResourceLocation(CobblemonIntegrations.MOD_ID, "textures/gui/evoitems.png");

    public EvoItemsJeiCategory() {
        super("cobblemon_evoitems",
                CobblemonJeiPlugin.getJeiHelpers().getGuiHelper().createDrawableItemStack(CobblemonItems.DAWN_STONE.getDefaultInstance()),
                CobblemonJeiPlugin.getJeiHelpers().getGuiHelper().createDrawable(GUI_LOCATION, 0, 0, 152, 95));
    }

    @Override
    public @NotNull RecipeType<EvoItemsWrapper> getRecipeType() {
        return CobblemonJeiPlugin.EVO_ITEMS_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EvoItemsWrapper wrapper, IFocusGroup focuses) {
        var items = wrapper.getValidItems();
        builder.addSlot(RecipeIngredientRole.INPUT, 68, 49)
                .setSlotName("evo_item")
                .addItemStacks(items.stream().map(Item::getDefaultInstance).toList());
        // TODO: Add tooltip showing additional conditions
    }
}
