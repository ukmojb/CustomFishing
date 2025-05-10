package com.wdcftgg.customfishing;


import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = CustomFishing.MODID)
public final class CFConfig {

    @Config.Comment("If true, the hooked item is permanently invincible")
    @Config.RequiresWorldRestart
    public static boolean itemInvulnerable = false;

    @Config.Comment("If true, only one \"fish\" will be caught at a time")
    @Config.RequiresWorldRestart
    public static boolean OneAtATime = false;

    @Config.Comment("If true, you will be able to fish in any liquid (the effect is equivalent to the vanilla).")
    @Config.RequiresWorldRestart
    public static boolean FishingInAnyLiquid = false;

    @Config.Comment("If true, the fishing conditions that are not met will be indicated when fishing")
    @Config.RequiresWorldRestart
    public static boolean EnableTip = true;

    @Config.Comment("I found that there is no fixed parameter to determine the degree of deceleration of different liquids\n" +
            "Otherwise, the default value will be used\n" +
            "This determines the initial speed at which the catch is flown(addition, not multiplication)\n" +
            "e.g:[liquid] -> [speed]")
    @Config.RequiresWorldRestart
    public static String[] liquidSpeeds = new String[] { "minecraft:water -> 1.0" };

    public static Map<String, Float> getliquidSpeedMap() {
        Map<String, Float> map = new HashMap<>();
        for (String str : liquidSpeeds) {
            String[] strs = str.split(" -> ");
            try{
                String liquid = strs[0];
                float speed = Float.valueOf(strs[1]);
                map.put(liquid, speed);

            }catch (ArrayIndexOutOfBoundsException e){
                CustomFishing.log("\"" + str + "\" is error");
            }

        }
        return map;
    }
}
