package com.wdcftgg.customfishing.mods.jei.fishingcondition;

import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FishingConditionRecipe implements IRecipeWrapper {
    protected List<List<ItemStack>> in;
    protected ItemStack outItem;
    protected ResourceLocation outPool;

    private FishingConditionRecipe(FishingCondition fishingCondition) {
        in = new ArrayList<>();
//        for (ItemStack stack : input) in.add(Collections.singletonList(stack));
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, outItem);
        ingredients.setInputLists(VanillaTypes.ITEM, in);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
//        if (info == null) return;
//
//        String text = I18n.format(info);
//
//        int width = minecraft.fontRenderer.getStringWidth(text);
//        int x = (int) ((recipeWidth - width) + 25);
//        int y = 4;
//
//        minecraft.fontRenderer.drawString(text, x - 30, y, Color.BLACK.getRGB());

    }

}
