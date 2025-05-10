package com.wdcftgg.customfishing.mods.jei;


import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.mods.jei.fishingcondition.FishingConditionCategory;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEICompat implements IModPlugin {

    public static final String FC = CustomFishing.MODID + ".fishingcondition";


    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        final IJeiHelpers helpers = registry.getJeiHelpers();

        final IGuiHelper gui = helpers.getGuiHelper();

        registry.addRecipeCategories(new FishingConditionCategory(gui));
    }

    @Override
    public void register(IModRegistry registry) {
        final IIngredientRegistry ingredientRegistry = registry.getIngredientRegistry();
        final IJeiHelpers jeiHelpers = registry.getJeiHelpers();

        IRecipeTransferRegistry recipeTransfer = registry.getRecipeTransferRegistry();

        registry.addRecipes(RecipeMaker.getWashingRecipes(jeiHelpers), RecipeCategories.WASHING_BY_FAN);

    }

    private
}