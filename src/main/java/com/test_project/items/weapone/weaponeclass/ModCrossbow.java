package com.test_project.items.weapone.weaponeclass;

import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;

public class ModCrossbow extends CrossbowItem {
    private final WeaponFeatureSet featureSet;

    public ModCrossbow(Item.Properties properties, WeaponFeatureSet features) {
        super(properties);
        this.featureSet = features;
    }

    public WeaponFeatureSet getFeatureSet() {
        return featureSet;
    }

}
