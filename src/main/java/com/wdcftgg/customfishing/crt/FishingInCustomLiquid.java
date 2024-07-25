package com.wdcftgg.customfishing.crt;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.List;

@ZenRegister
@ZenClass("mods.customfishing.FishingInCustomLiquid")
public class FishingInCustomLiquid {

    @ZenMethod
    public static void inLiquidItemChancePoolDimBiome(ILiquidStack liquid, String pool, IItemStack iitemStack, Float chance, String biomeid, Integer dimid) {
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

        FishingCondition fishingCondition =
                new FishingCondition(
                        CraftTweakerMC.getFluid(liquid.getDefinition()).getBlock().getRegistryName().toString(), new ResourceLocation(namespaceIn, pathIn),
                        CraftTweakerMC.getItemStack(iitemStack),
                        chance, biomeid, dimid);
        FishingConditionInit.registryFishingCondition(fishingCondition);
    }
}
