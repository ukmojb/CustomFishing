package com.wdcftgg.customfishing.proxy;

import com.wdcftgg.customfishing.CustomFishing;
import com.wdcftgg.customfishing.entity.CFEntityFishHook;
import com.wdcftgg.customfishing.entity.ModEntityInit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonProxy {

    public void onInit(){

    }



    public void onPreInit() {
//        ModEntityInit.registerEntities();
        String EntityCFFishHookName = "cffishhook";
       ResourceLocation EntityCFFishHookResource = new ResourceLocation(CustomFishing.MODID + ":cffishhook");

        EntityRegistry.registerModEntity(EntityCFFishHookResource, CFEntityFishHook.class, EntityCFFishHookName, 0,CustomFishing.MODID, 100, 1, true);
    }

    public void onPostInit() {

    }
}
