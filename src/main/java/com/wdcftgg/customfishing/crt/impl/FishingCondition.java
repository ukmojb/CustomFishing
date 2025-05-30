package com.wdcftgg.customfishing.crt.impl;

import com.wdcftgg.customfishing.crt.FishingConditionInit;
import com.wdcftgg.customfishing.crt.api.IFishingCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FishingCondition implements IFishingCondition {

    public final Fluid fluid;
    public final ItemStack itemStack;
    public final ResourceLocation lootRes;
    public String biome = null;
    public Integer dimension = null;
    public Integer damage = null;
    public List<Item> fishRods = new ArrayList<>();
    public List<Item> fishBaits = new ArrayList<>();
    public Boolean isDay = null;
    public Integer altitudeBegin = null;
    public Integer altitudeEnd = null;
    public Float chance = null;

    public FishingCondition(Fluid fluid, ItemStack itemStack){
        this.fluid = fluid;
        this.itemStack = itemStack;
        this.lootRes = null;
    }

    public FishingCondition(Fluid fluid, ResourceLocation lootRes){
        this.fluid = fluid;
        this.itemStack = ItemStack.EMPTY;
        this.lootRes = lootRes;
    }

    @Override
    public IFishingCondition setDimension(int dimension) {
        this.dimension = dimension;
        return this;
    }

    @Override
    public IFishingCondition setBiome(String biome) {
        this.biome = biome;
        return this;
    }

    @Override
    public IFishingCondition setChance(float chance) {
        this.chance = chance;
        return this;
    }

    @Override
    public IFishingCondition additionalDamage(int damage) {
        this.damage = damage;
        return this;
    }

    @Override
    public IFishingCondition setFishRod(IItemStack iitemStack) {
        this.fishRods.add(CraftTweakerMC.getItemStack(iitemStack).getItem());
        return this;
    }

    @Override
    public IFishingCondition addFishRods(IItemStack[] iitemStacks) {
        ItemStack[] itemStacks = CraftTweakerMC.getItemStacks(iitemStacks);
        this.fishRods.addAll(Arrays.stream(itemStacks).map(ItemStack::getItem).collect(Collectors.toList()));
        return this;
    }

    @Override
    public IFishingCondition setAltitude(int begin, int end) {
        this.altitudeBegin = begin;
        this.altitudeEnd = end;
        return this;
    }

    @Override
    public IFishingCondition setDay(boolean isDay) {
        this.isDay = isDay;
        return this;
    }

    @Override
    public IFishingCondition setFishBait(IItemStack iitemStack) {
        this.fishBaits.add(CraftTweakerMC.getItemStack(iitemStack).getItem());
        return this;
    }

    @Override
    public IFishingCondition setFishBaits(IItemStack[] iitemStacks) {
        ItemStack[] itemStacks = CraftTweakerMC.getItemStacks(iitemStacks);
        this.fishBaits.addAll(Arrays.stream(itemStacks).map(ItemStack::getItem).collect(Collectors.toList()));
        return this;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ResourceLocation getLootRes() {
        return lootRes;
    }

    public String getBiome() {
        return biome;
    }

    public int getDimension() {
        return dimension;
    }

    public int getDamage() {
        return damage;
    }

    public List<Item> getFishRods() {
        return fishRods;
    }

    public List<Item> getFishBaits() {
        return fishBaits;
    }

    public boolean isDay() {
        return isDay;
    }

    public int getAltitudeBegin() {
        return altitudeBegin;
    }

    public int getAltitudeEnd() {
        return altitudeEnd;
    }

    public float getChance() {
        return chance;
    }

    @Override
    public void register() {
        FishingConditionInit.registryFishingCondition(this);
    }
}
