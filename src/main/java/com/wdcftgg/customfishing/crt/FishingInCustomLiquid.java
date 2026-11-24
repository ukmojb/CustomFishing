package com.wdcftgg.customfishing.crt;

import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.customfishing.FishingInCustomLiquid")
public class FishingInCustomLiquid {

    @ZenMethod
    public static void inLiquidItemChancePoolDimBiome(ILiquidStack liquid, String pool, IItemStack iitemStack, Float chance, String biomeid, Integer dimid) {
        Fluid fluid = liquid == null ? null : CraftTweakerMC.getFluid(liquid.getDefinition());
        FishingCondition fishingCondition;
        if (pool != null && !pool.isEmpty()) {
            fishingCondition = new FishingCondition(fluid, new ResourceLocation(pool));
        } else if (iitemStack != null) {
            fishingCondition = new FishingCondition(fluid, CraftTweakerMC.getItemStack(iitemStack));
        } else {
            CraftTweakerAPI.logError("inLiquidItemChancePoolDimBiome 需要物品或战利品表");
            return;
        }

        if (chance != null) fishingCondition.setChance(chance);
        if (biomeid != null && !biomeid.isEmpty()) fishingCondition.setBiome(biomeid);
        if (dimid != null) fishingCondition.setDimension(dimid);

        FishingConditionInit.registryFishingCondition(fishingCondition);
    }
}
