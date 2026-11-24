package com.wdcftgg.customfishing.mods.jei.fishingcondition;

import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import com.wdcftgg.customfishing.util.FakeClientWorld;
import com.wdcftgg.customfishing.util.Tools;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FishingConditionRecipe implements IRecipeWrapper {

    public Fluid fluid;
    public ItemStack itemStack;
    public String biome = null;
    public ResourceLocation pool = null;
    public Integer dimension = null;
    public Integer damage = null;
    public List<Item> fishRods = new ArrayList<>();
    public List<Item> fishBaits = new ArrayList<>();
    public Boolean isDay = null;
    public Integer altitudeBegin = null;
    public Integer altitudeEnd = null;
    public Float chance = null;

    public FishingConditionRecipe(FishingCondition fishingCondition) {
        fluid = fishingCondition.fluid;
        itemStack = fishingCondition.itemStack;
        biome = fishingCondition.biome;
        pool = fishingCondition.lootRes;
        dimension = fishingCondition.dimension;
        damage = fishingCondition.damage;
        fishRods = fishingCondition.fishRods;
        fishBaits = fishingCondition.fishBaits;
        isDay = fishingCondition.isDay;
        altitudeBegin = fishingCondition.altitudeBegin;
        altitudeEnd = fishingCondition.altitudeEnd;
        chance = fishingCondition.chance;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {

        ItemStack fluiditem;

        if (fluid == null) {
            fluiditem = new ItemStack(Items.BUCKET).setStackDisplayName("任意液体");
        } else if (fluid == FluidRegistry.WATER) {
            fluiditem = new ItemStack(Items.WATER_BUCKET);
        } else if (fluid == FluidRegistry.LAVA) {
            fluiditem = new ItemStack(Items.LAVA_BUCKET);
        } else {
            fluiditem = FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
        }


        ItemStack timeitem = Tools.addLore(new ItemStack(Items.CLOCK).setStackDisplayName("时间需求"), Collections.singletonList("需求时间：" + (isDay == null ? "任意时间" : (isDay ? "白天" : "夜晚"))));
        ItemStack altitudeitem = Tools.addLore(new ItemStack(Items.COMPASS).setStackDisplayName("纬度需求"), Collections.singletonList((altitudeEnd == null ? "任意高度" : "需求最低高度：" + altitudeBegin + "\n需求最高高度：" + altitudeEnd)));
        ItemStack dimensionitem = Tools.addLore(new ItemStack(Blocks.GRASS).setStackDisplayName("维度需求"), Collections.singletonList("需求维度：" + (dimension == null ? "任意维度" : dimension)));
        ItemStack biomeitem = Tools.addLore(new ItemStack(Blocks.PUMPKIN).setStackDisplayName("生物群系需求"), Collections.singletonList("需求群系：" + (biome == null ? "任意群系" : biome)));
        ItemStack chanceitem = Tools.addLore(new ItemStack(Items.CHORUS_FRUIT).setStackDisplayName("上钩概率"), Collections.singletonList(chance == null ? "" : chance * 100 + "%"));

        List<ItemStack> itemStacks = new ArrayList<>();

        itemStacks.add(fluiditem);
        itemStacks.add(timeitem);
        itemStacks.add(altitudeitem);
        itemStacks.add(dimensionitem);
        itemStacks.add(biomeitem);

        if (chance != null) itemStacks.add(chanceitem);

        ingredients.setInputs(VanillaTypes.ITEM, itemStacks);

        List<ItemStack> outList = new ArrayList<>();

        if (!itemStack.isEmpty()) {
            outList.add(itemStack);
        }

        if (pool != null) {
            for (LootPool lootPool : Objects.requireNonNull(getPools(getManager().getLootTableFromLocation(pool)))) {
                for (LootEntry lootEntry : Objects.requireNonNull(CustomFishing.getLootEntries(lootPool))) {
                    if (lootEntry instanceof LootEntryItem) {
                        LootEntryItem lootEntryItem = (LootEntryItem) lootEntry;

                        for (LootFunction lootfunction : lootEntryItem.functions)
                        {
                            List<Integer> metaList = new ArrayList<>();
                            if (lootfunction instanceof SetMetadata) {
                                SetMetadata metadata = (SetMetadata) lootfunction;
                                for (int i = (int) metadata.metaRange.getMin(); i < (int) metadata.metaRange.getMax(); i++) {
                                    metaList.add(i);
                                }
                            }

                            if (!metaList.isEmpty()) {
                                for (Integer meta : metaList) {
                                    ItemStack itemStack1 = new ItemStack(lootEntryItem.item, 1, meta);
                                    outList.add(itemStack1);
                                }
                            } else {
                                ItemStack itemStack1 = new ItemStack(lootEntryItem.item, 1, 0);
                                outList.add(itemStack1);
                            }
                        }
                    }
                }
            }
        }


        ingredients.setOutputs(VanillaTypes.ITEM, outList);
    }

    public static List<LootPool> getPools(LootTable table) {
        return CustomFishing.getPools(table);
    }

    private static LootTableManager manager;

    public static LootTableManager getManager(@Nullable World world) {
        if (world == null || world.getLootTableManager() == null) {
            if (manager == null) {
                ISaveHandler saveHandler = FakeClientWorld.saveHandler;
                manager = new LootTableManager(new File(new File(saveHandler.getWorldDirectory(), "data"), "loot_tables"));
            }
            return manager;
        }
        return world.getLootTableManager();
    }

    public static LootTableManager getManager() {
        return getManager(Tools.getWorld());
    }

}
