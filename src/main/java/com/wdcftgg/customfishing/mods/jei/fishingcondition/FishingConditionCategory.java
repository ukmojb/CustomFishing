package com.wdcftgg.customfishing.mods.jei.fishingcondition;

import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.mods.jei.JEICompat;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.config.Constants;
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
        ResourceLocation location = new ResourceLocation(CustomFishing.MODID, "textures/gui/jei/fishing.png");
        this.background = helper.drawableBuilder(location, 0, 0, 162,126).setTextureSize(162,126).build();
//        background = helper.createBlankDrawable(162,126);
        icon = helper.createDrawableIngredient(new ItemStack(Items.FISHING_ROD));
    }

    @Override
    public String getUid() {
        return JEICompat.FISHING;
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

        List<List<ItemStack>> inputs = iIngredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = iIngredients.getOutputs(VanillaTypes.ITEM);

        int id = 0;

        stacks.init(id++, true, 0, -2);
        stacks.init(id++, true, 18, -2);
        stacks.init(id++, true, 36, -2);
        stacks.init(id++, true, 54, -2);
        stacks.init(id++, true, 72, -2);
//        stacks.init(id++, true, 0, 18);
//        stacks.init(id++, true, 0, 36);

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                stacks.init(id++, false, (18 * x),18 + (18 * y));
            }
        }
        stacks.set(iIngredients);
    }

//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, Wrapper recipeWrapper, IIngredients ingredients) {
//        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
//        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
//        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//        //Output
//        int offset1 = outputs.size() - 1;
//        for (int i = 0; i <= offset1; i++) guiItemStacks.init(i, false, 102 + (offset1 - i) * 17, 9);
//        //Input
//        int offset0 = inputs.size() - 1;
//        for (int i = 0; i <= offset0; i++) guiItemStacks.init(i + 5, true, 56 - (offset0 - i) * 17, 9);
//
//        guiItemStacks.set(ingredients);
//    }
}