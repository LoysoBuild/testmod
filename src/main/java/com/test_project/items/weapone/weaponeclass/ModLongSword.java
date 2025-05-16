package com.test_project.items.weapone.weaponeclass;

import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.item.Tier;

public class ModLongSword extends ModSword {
    public ModLongSword(Tier tier, float attackDamage, float attackSpeed, int durability, double attackRange, WeaponFeatureSet features) {
        super(tier, attackDamage, attackSpeed, durability, attackRange, features);
    }
}

