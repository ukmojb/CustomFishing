package com.wdcftgg.customfishing.crt;

import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FishingCondition {
    private String liquid;
    private ResourceLocation lootRes;
    private ItemStack itemStack;
    private Float chance;
    private String biomeid;
    private Integer dimid;

    public FishingCondition(String liquid, ResourceLocation lootRes, ItemStack itemStack, Float chance, String biomeid, Integer dimid) {
        this.liquid = liquid;
        this.lootRes = lootRes;
        this.itemStack = itemStack;
        this.chance = chance;
        this.biomeid = biomeid;
        this.dimid = dimid;
    }

    public String getLiquid() {
        return liquid;
    }

    public ResourceLocation getLootRes() {
        return lootRes;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Float getChance() {
        return chance;
    }

    public String getBiomeid() {
        return biomeid;
    }

    public Integer getDimid() {
        return dimid;
    }
}
