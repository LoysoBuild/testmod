package com.test_project.items.weapone.feature;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HammerSmashFeature implements WeaponFeature {
    @Override
    public String getId() {
        return "hammer_smash";
    }

    @Override
    public void onAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (!attacker.level().isClientSide()) {
            // Сила отбрасывания (чем больше, тем дальше)
            float knockbackStrength = 10.5F;
            // Направление: используем yaw атакующего (куда он смотрит)
            double yaw = Math.toRadians(attacker.getYRot());
            double x = -Math.sin(yaw);
            double z = Math.cos(yaw);

            // Используем ванильный метод knockback для правильной обработки
            target.knockback(knockbackStrength, x, z);

            // (Необязательно) Добавьте звук для эффекта
            // target.level().playSound(null, target.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
