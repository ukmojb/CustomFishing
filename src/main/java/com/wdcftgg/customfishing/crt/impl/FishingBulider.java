package com.wdcftgg.customfishing.crt.impl;

import com.wdcftgg.customfishing.crt.api.IFishingCondition;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenMethodStatic;

import java.util.Arrays;
import java.util.List;

@ZenRegister
@ZenClass("mods.customfishing.FishingBulider")
public class FishingBulider {


    @ZenMethod
    public static IFishingCondition create(ILiquidStack liquid, IItemStack iitemStack) {
        return new FishingCondition(CraftTweakerMC.getFluid(liquid.getDefinition()), CraftTweakerMC.getItemStack(iitemStack));
    }

    @ZenMethod
    public static IFishingCondition create(ILiquidStack liquid, String pool) {
        List<String> stringList = Arrays.asList(pool.split(":"));
        String namespaceIn;
        String pathIn;
        if (stringList.size() <= 1) {
            namespaceIn = "minecraft";
            pathIn = pool;
        } else {
            namespaceIn = stringList.get(0);
            pathIn =  stringList.get(1);
        }
        return new FishingCondition(CraftTweakerMC.getFluid(liquid.getDefinition()), new ResourceLocation(namespaceIn, pathIn));
    }
}
