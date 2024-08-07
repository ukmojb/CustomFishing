package com.wdcftgg.customfishing;

import com.wdcftgg.customfishing.entity.CFEntityFishHook;
import com.wdcftgg.customfishing.entity.ModEntityInit;
import com.wdcftgg.customfishing.proxy.CommonProxy;
import com.wdcftgg.customfishing.proxy.ServerProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
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
    public static final String VERSION = "0.0.5";

    @Mod.Instance
    public static CustomFishing instance;

    public static final String CLIENT_PROXY_CLASS = "com.wdcftgg.customfishing.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.wdcftgg.customfishing.proxy.ServerProxy";

    public static Map<EntityPlayer, CFEntityFishHook> fishEntity = new HashMap<>();

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    public static ServerProxy serverProxy = new ServerProxy();



    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModEntityInit.registerEntities();

        proxy.onPreInit();
        serverProxy.onPreInit();
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.onInit();
        serverProxy.onInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.onPostInit();
        serverProxy.onPostInit();
    }

}
