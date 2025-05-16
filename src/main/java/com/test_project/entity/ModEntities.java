package com.test_project.entity;

import com.test_project.MainMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.bus.api.IEventBus;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MainMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<TestMobEntity>> TEST_MOB =
            ENTITY_TYPES.register("test_mob", () ->
                    EntityType.Builder.of(TestMobEntity::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f)
                            .build("mainmod:test_mob") // ← передаём id сущности
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
