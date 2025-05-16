package com.test_project.items.weapone.hammer;


import com.test_project.items.weapone.feature.WeaponFeatureSet;
import com.test_project.items.weapone.sword.ModSword;
import net.minecraft.world.item.Tier;

public class ModHammer extends ModSword {
    public ModHammer(Tier tier, float attackDamage, float attackSpeed, int durability, double attackRange, WeaponFeatureSet features) {
        super(tier, attackDamage, attackSpeed, durability, attackRange, features);
    }

    // Здесь можно добавить уникальные методы или визуальные эффекты для молота
}
