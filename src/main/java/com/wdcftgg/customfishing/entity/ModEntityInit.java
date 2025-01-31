package com.wdcftgg.customfishing.entity;

import com.wdcftgg.customfishing.CustomFishing;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ModEntityInit {
    private static int ENTITY_NEXT_ID = 1;
    public static final String EntityFMBLavaFishHookName = "cffishhook";
    public static final ResourceLocation EntityFishHookResource = new ResourceLocation(CustomFishing.MODID + ":cffishhook");

    private ModEntityInit() {}

    public static void register() {
        EntityRegistry.registerModEntity(EntityFishHookResource, CFEntityFishHook.class, EntityFMBLavaFishHookName, 0, CustomFishing.MODID, 100, 1, true);
    }
}