package com.test_project.items.weapone.weaponeclass;

import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;

public class ModBow extends BowItem {
    private final WeaponFeatureSet featureSet;

    public ModBow(Item.Properties properties, WeaponFeatureSet features) {
        super(properties);
        this.featureSet = features;
    }

    public WeaponFeatureSet getFeatureSet() {
        return featureSet;
    }
}

