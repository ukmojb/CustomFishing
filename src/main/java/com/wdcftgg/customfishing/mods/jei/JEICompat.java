package com.wdcftgg.customfishing.mods.jei;


import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.mods.jei.fishingcondition.FishingConditionCategory;
import com.wdcftgg.customfishing.mods.jei.fishingcondition.RecipeMaker;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@JEIPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JEICompat implements IModPlugin {

    public static final String FISHING = CustomFishing.MODID + ".fishingcondition";


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

        registry.addRecipes(RecipeMaker.getFishingConditionRecipes(jeiHelpers), FISHING);

        registry.addRecipeCatalyst(new ItemStack(Items.FISHING_ROD), new String[]{FISHING});

    }

}