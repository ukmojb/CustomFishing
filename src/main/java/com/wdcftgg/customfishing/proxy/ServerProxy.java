package com.wdcftgg.customfishing.proxy;

import com.wdcftgg.customfishing.entity.ModEntityInit;

public class ServerProxy extends CommonProxy {

    private static int ENTITY_NEXT_ID = 1;

    @Override
    public void onInit() {

    }

    @Override
    public void onPreInit() {
        ModEntityInit.registerEntities();
    }

    @Override
    public void onPostInit() {

    }


}
