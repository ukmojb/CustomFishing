package com.wdcftgg.customfishing;

import com.wdcftgg.customfishing.entity.CFEntityFishHook;
import com.wdcftgg.customfishing.entity.ModEntityInit;
import com.wdcftgg.customfishing.proxy.CommonProxy;
import com.wdcftgg.customfishing.proxy.ServerProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/8/22 19:11
 */
@Mod(modid = CustomFishing.MODID, name = CustomFishing.NAME, version = CustomFishing.VERSION, dependencies="required-after:crafttweaker")
public class CustomFishing {
    private static Logger logger;

    public static final String MODID = "customfishing";
    public static final String NAME = "CustomFishing";
    public static final String VERSION = "0.1.3";
    private static Field pools;
    private static Field lootEntries;
    public static Map<String, LootTable> tables = new HashMap<>();

    @Mod.Instance
    public static CustomFishing instance;

    public static final String CLIENT_PROXY_CLASS = "com.wdcftgg.customfishing.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.wdcftgg.customfishing.proxy.CommonProxy";


    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    public static ServerProxy serverProxy = new ServerProxy();



    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {


        proxy.onPreInit();
//        serverProxy.onPreInit();
        logger = event.getModLog();

        pools = ReflectionHelper.findField(LootTable.class, "pools", "field_186466_c", "c");
        lootEntries = ReflectionHelper.findField(LootPool.class, "lootEntries", "field_186453_a", "a");

    }

    public static List<LootPool> getPools(LootTable table) {

        try {

            return (List<LootPool>) pools.get(table);
        }

        catch (IllegalArgumentException | IllegalAccessException e) {

            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<LootEntry> getLootEntries(LootPool pool) {

        try {
            return (List<LootEntry>) lootEntries.get(pool);
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.onInit();
//        serverProxy.onInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.onPostInit();
//        serverProxy.onPostInit();
    }

    public static void log(String str) {
        logger.info(str);
    }

}
