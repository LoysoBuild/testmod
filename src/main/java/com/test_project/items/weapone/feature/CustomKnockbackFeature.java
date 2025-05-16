package com.test_project.items.weapone.feature;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public class CustomKnockbackFeature implements WeaponFeature {
    @Override
    public String getId() { return "custom_knockback"; }

    @Override
    public void onAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (!attacker.level().isClientSide()) {
            double resistance = 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            if (resistance > 0) {
                Vec3 look = attacker.getLookAngle().normalize();
                double strength = 2.5 * resistance;
                target.push(look.x * strength, 0.4, look.z * strength);
                target.hasImpulse = true;
            }
        }
    }
}
