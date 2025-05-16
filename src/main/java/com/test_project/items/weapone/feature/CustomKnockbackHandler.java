package com.test_project.items.weapone.feature;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.test_project.items.weapone.hammer.ModHammer;

public class CustomKnockbackHandler {
    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        LivingEntity target = event.getEntity();
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        if (!(weapon.getItem() instanceof ModHammer hammer)) return;

        if (!hammer.getFeatureSet().has("custom_knockback")) return;

        hammer.getFeatureSet().getAll().forEach(f -> f.onAttack(attacker, target, weapon));
    }
}
