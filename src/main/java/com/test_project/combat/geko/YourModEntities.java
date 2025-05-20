package com.test_project.combat.geko;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.test_project.combat.geko.PlayerAnimatable;

public class YourModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "yourmodid");

    public static final DeferredHolder<EntityType<?>, EntityType<PlayerAnimatable>> PLAYER_ANIMATABLE =
            ENTITIES.register("player_animatable",
                    () -> EntityType.Builder.<PlayerAnimatable>of(PlayerAnimatable::new, MobCategory.MISC)
                            .sized(0.6f, 1.8f)
                            .build("player_animatable")
            );

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }
}
