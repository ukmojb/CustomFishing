package com.wdcftgg.customfishing.proxy;

import com.wdcftgg.customfishing.cilent.RenderCFFish;
import com.wdcftgg.customfishing.entity.CFEntityFishHook;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    public void onInit(){

    }



    public void onPreInit() {

        RenderingRegistry.registerEntityRenderingHandler(CFEntityFishHook.class, RenderCFFish::new);

    }

    public void onPostInit() {

    }

}
