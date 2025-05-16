package com.test_project.items.weapone.weaponeclass;

import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.neoforged.fml.common.Mod;

public class ModBowLong extends BowItem {
    private final WeaponFeatureSet featureSet;

    public ModBowLong(Item.Properties properties, WeaponFeatureSet features) {
        super(properties);
        this.featureSet = features;
    }

    public WeaponFeatureSet getFeatureSet() {
        return featureSet;
    }
}
