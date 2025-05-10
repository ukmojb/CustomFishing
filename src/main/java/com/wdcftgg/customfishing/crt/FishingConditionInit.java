package com.wdcftgg.customfishing.crt;

import com.wdcftgg.customfishing.crt.impl.FishingCondition;

import java.util.ArrayList;
import java.util.List;

public class FishingConditionInit {
    private static List<FishingCondition> conditionList = new ArrayList<>();

    public static void registryFishingCondition(FishingCondition fishingCondition){
        conditionList.add(fishingCondition);
    }

    public static List<FishingCondition> getAllFishingCondition(){
        return conditionList;
    }
}
