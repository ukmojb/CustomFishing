package com.wdcftgg.customfishing.mods.jei.fishingcondition;

import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.crt.impl.FishingCondition;
import com.wdcftgg.customfishing.util.FakeClientWorld;
import com.wdcftgg.customfishing.util.Tools;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

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
//        in = new ArrayList<>();
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

//        if (fluid.getName().equals("water")) {
//            fluiditem = new ItemStack(Blocks.WATER);
//        } else if (fluid.getName().equals("lava")) {
//            fluiditem = new ItemStack(Blocks.LAVA);
//        } else {
//            fluiditem = new ItemStack(fluid.getBlock());
//        }

        switch (fluid.getName()) {
            case "water":
                fluiditem = new ItemStack(Items.WATER_BUCKET);
                break;
            case "lava":
                fluiditem = new ItemStack(Items.LAVA_BUCKET);
                break;
            default:
                fluiditem = FluidUtil.getFilledBucket(new FluidStack(fluid, 1000));
                break;
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
//        ingredients.setInputLists(VanillaTypes.ITEM, in);
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

    public static List<LootPool> getPools(LootTable table) {
        try
        {
            Field field = ReflectionHelper.findField(LootTable.class, "pools", "field_186466_c");
            List<LootPool> pools = (List<LootPool>) field.get(table);
            return pools;
        }
        catch (Exception e)
        {
            return null;
//            throw new RuntimeException(e);
        }
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
