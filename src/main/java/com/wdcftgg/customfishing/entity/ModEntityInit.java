package com.wdcftgg.customfishing.entity;

import com.wdcftgg.customfishing.CustomFishing;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod.EventBusSubscriber
public class ModEntityInit {
    private static int ENTITY_NEXT_ID = 1;
    public static void registerEntities()
    {

        registerEntity("CFEntityFishHook", CFEntityFishHook.class);

    }

    private  static  void registerEntityAndEgg(String name, Class<? extends Entity> entity)
    {
        registerEntityAndEgg(name, entity, ENTITY_NEXT_ID, 50, 0x3786e7, 0x660000);
    }

    private  static  void registerEntityAndEgg(String name, Class<? extends Entity> entity, int color1, int color2)
    {
        registerEntityAndEgg(name, entity, ENTITY_NEXT_ID, 50, color1, color2);
    }

    private  static  void registerEntity(String name, Class<? extends Entity> entity)
    {
        registerEntityNoEgg(name, entity, ENTITY_NEXT_ID, 50);
    }

    private  static  void registerEntityAndEgg(String name, Class<? extends Entity> entity, int id, int range, int color1, int color2){
        EntityRegistry.registerModEntity(new ResourceLocation(CustomFishing.MODID + ":" + name),
                entity,
                name,
                id,
                CustomFishing.instance,
                range,
                1,
                true,
                color1, color2
        );
        ENTITY_NEXT_ID++;
    }

    private  static  void registerEntityNoEgg(String name, Class<? extends Entity> entity, int id, int range){
        EntityRegistry.registerModEntity(new ResourceLocation(CustomFishing.MODID + ":" + name),
                entity,
                name,
                id,
                CustomFishing.instance,
                range,
                1,
                true
        );
        ENTITY_NEXT_ID++;
    }
}