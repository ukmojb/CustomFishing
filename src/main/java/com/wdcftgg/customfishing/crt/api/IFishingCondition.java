package com.wdcftgg.customfishing.crt.api;


import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.customfishing.FishingCondition")
public interface IFishingCondition {

    @ZenMethod
    IFishingCondition setDimension(int dimension);

    @ZenMethod
    IFishingCondition setBiome(String biome);

    @ZenMethod
    IFishingCondition setChance(float chance);

    @ZenMethod
    IFishingCondition additionalDamage(int damage);

    @ZenMethod
    IFishingCondition addFishRod(IItemStack iitemStack);

    @ZenMethod
    IFishingCondition addFishRods(IItemStack[] iitemStacks);

    //begin应该是较低的，end应该是较高的
    @ZenMethod
    IFishingCondition setAltitude(int begin, int end);

    @ZenMethod
    IFishingCondition setDay(boolean isDay);

    @ZenMethod
    IFishingCondition addFishBait(IItemStack iitemStack);

    @ZenMethod
    IFishingCondition addFishBaits(IItemStack[] iitemStacks);

    @ZenMethod
    IFishingCondition setEntityData(IData nbt);

    @ZenMethod
    void register();
}
