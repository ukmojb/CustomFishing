package com.wdcftgg.customfishing.util;

import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;

public class Tools {

    public static World getWorld() {
        World world = Minecraft.getMinecraft().world;
        if (world == null) {
            world = new FakeClientWorld();
        }
        return world;
    }

    public static ItemStack inventoryGetItem(IInventory inventory, Item item) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != ItemStack.EMPTY) {
                if (itemStack.getItem().equals(item)) {
                    return itemStack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static void damageFishRod(List<Item> itemStacks, EntityPlayer player, int damage) {
        ItemStack[] handsItem = new ItemStack[]{player.getHeldItemMainhand(), player.getHeldItemOffhand()};

        if (!itemStacks.isEmpty()) {
            for (ItemStack itemStack : handsItem) {
                if (itemStacks.contains(itemStack.getItem())) {
                    itemStack.damageItem(damage, player);
                    break;
                }
            }
        }
    }

    public static void removeFishBaits(List<Item> itemStacks, IInventory inventory) {
        if (!itemStacks.isEmpty()) {
            for (Item item : itemStacks) {
                ItemStack fishBait = inventoryGetItem(inventory, item);
                if (fishBait != ItemStack.EMPTY && fishBait.getCount() >= 1) {
                    fishBait.setCount(fishBait.getCount() - 1);
                    break;
                }
            }
        }
    }

    public static ItemStack addLore(ItemStack itemStack, List<String> strings) {
        NBTTagCompound tagComp;

        if(!itemStack.hasTagCompound() || itemStack.getTagCompound() == null) {
            tagComp = new NBTTagCompound();
        } else {
            tagComp = itemStack.getTagCompound();
        }

        NBTTagCompound display;
        if(!tagComp.hasKey("display") || !(tagComp.getTag("display") instanceof NBTTagCompound)) {
            display = new NBTTagCompound();
        } else {
            display = (NBTTagCompound) tagComp.getTag("display");
        }

        NBTTagList loreList;
        if(!display.hasKey("Lore") || !(display.getTag("Lore") instanceof NBTTagList)) {
            loreList = new NBTTagList();
        } else {
            loreList = (NBTTagList) display.getTag("Lore");
        }

        for(String s : strings) {
            loreList.appendTag(new NBTTagString(s));
        }

        display.setTag("Lore", loreList);
        tagComp.setTag("display", display);

        ItemStack newStack = itemStack.copy();
        newStack.setTagCompound(tagComp);

        return newStack;
    }
}
