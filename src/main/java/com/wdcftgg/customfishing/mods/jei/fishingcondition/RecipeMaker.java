package com.wdcftgg.customfishing.mods.jei.fishingcondition;

import com.wdcftgg.customfishing.crt.FishingConditionInit;
import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipeMaker {
    public static List<FishingConditionRecipe> getFishingConditionRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
//        SandingRecipes instance = SandingRecipes.instance;
//
//        Map<ItemStack, ItemStack> recipeMap = instance.recipes;
        List<FishingConditionRecipe> recipeList = new ArrayList<>();

        for (FishingCondition fishingCondition : FishingConditionInit.getAllFishingCondition()) {
            recipeList.add(new FishingConditionRecipe(fishingCondition));
        }
//
//        for (Map.Entry<ItemStack, ItemStack> entry : recipeMap.entrySet()) {
//            recipeList.add(new SandingRecipe(entry.getKey(), entry.getValue()));
//        }

        return recipeList;
    }
}
