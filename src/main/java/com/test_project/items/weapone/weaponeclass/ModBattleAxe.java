package com.test_project.items.weapone.weaponeclass;

import com.test_project.items.weapone.AbstractWeapon;
import com.test_project.items.weapone.feature.WeaponFeatureSet;
import net.minecraft.world.item.Tier;

public class ModBattleAxe extends AbstractWeapon {
    public ModBattleAxe(
            Tier tier,
            float attackDamage,
            float attackSpeed,
            int durability,
            double attackRange,
            WeaponFeatureSet features,
            String defaultComboId
    ) {
        super(tier, attackDamage, attackSpeed, durability, attackRange, features, defaultComboId);
    }
}
