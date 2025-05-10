package com.wdcftgg.customfishing.mods.jei.fishingcondition;

import com.wdcftgg.customfishing.mods.jei.JEICompat;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.runtime.JeiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FishingConditionCategory implements IRecipeCategory {

    private final IDrawable background;
    private final IDrawable icon;


    public FishingConditionCategory(IGuiHelper helper) {
        background = helper.createBlankDrawable(235,235);
        icon = helper.createDrawableIngredient(new ItemStack(Items.FISHING_ROD));
    }

    @Override
    public String getUid() {
        return JEICompat.FC;
    }

    @Override
    public String getTitle() {
        return "钓鱼";
    }

    @Override
    public String getModName() {
        return "customfishing";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper iRecipeWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        int id = 0;

        stacks.init(id++, true, 0, 0);
        stacks.init(id++, true, 18, 0);
        stacks.init(id++, true, 36, 0);
        stacks.init(id++, true, 54, 0);
        stacks.init(id++, true, 72, 0);
        stacks.init(id++, true, 0, 18);
        stacks.init(id++, true, 0, 36);

        for (int y = 1; y < 9; y++) {
            for (int x = 1; x < 9; x++) {
                stacks.init(id++, false, 1 + (25 * x),1 + (25 * y));
            }
        }
    }

}