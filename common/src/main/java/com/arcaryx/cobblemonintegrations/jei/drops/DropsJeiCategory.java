package com.arcaryx.cobblemonintegrations.jei.drops;

import com.arcaryx.cobblemonintegrations.CobblemonIntegrations;
import com.arcaryx.cobblemonintegrations.jei.BlankJeiCategory;
import com.arcaryx.cobblemonintegrations.jei.CobblemonJeiPlugin;
import com.cobblemon.mod.common.CobblemonItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DropsJeiCategory extends BlankJeiCategory<DropsWrapper> {
    public static final ResourceLocation GUI_LOCATION = new ResourceLocation(CobblemonIntegrations.MOD_ID, "textures/gui/drops.png");

    public DropsJeiCategory() {
        super("cobblemon_drops",
                CobblemonJeiPlugin.getJeiHelpers().getGuiHelper().createDrawableItemStack(CobblemonItems.POKE_BALL.getDefaultInstance()),
                CobblemonJeiPlugin.getJeiHelpers().getGuiHelper().createDrawable(GUI_LOCATION, 0, 0, 140, 95));
    }

    @Override
    public @NotNull RecipeType<DropsWrapper> getRecipeType() {
        return CobblemonJeiPlugin.DROPS_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DropsWrapper wrapper, IFocusGroup focuses) {
        var xOffset = 0;
        var drops = wrapper.getDrops();
        var dropCount = Math.min(drops.size(), 4 * 4);
        for (var i = 0; i < 4; i++) {
            int yOffset = 0;
            for (var j = 0; j < 4; j++) {
                int slotNumber = i + j * 4;
                if (slotNumber < dropCount) {
                     builder.addSlot(RecipeIngredientRole.OUTPUT, 66 + xOffset, 13 + yOffset)
                        .setSlotName(String.valueOf(slotNumber))
                        .addRichTooltipCallback(wrapper)
                        .addItemStack(BuiltInRegistries.ITEM.get(drops.get(slotNumber).getItem()).getDefaultInstance());
                }
                yOffset += 80 / 4;
            }
            xOffset += 72 / 4;
        }
    }

}
