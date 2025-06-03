package com.test_project.entity;

import com.test_project.MainMod;
import com.test_project.entity.list.MirrorPlayerEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MainMod.MOD_ID);

    // Регистрация Mirror Player
    public static final DeferredHolder<EntityType<?>, EntityType<MirrorPlayerEntity>> MIRROR_PLAYER =
            ENTITY_TYPES.register("mirror_player", () ->
                    EntityType.Builder.of(MirrorPlayerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F) // Размер как у игрока
                            .build("mirror_player"));

    // Ваш существующий тестовый моб
    public static final DeferredHolder<EntityType<?>, EntityType<TestMobEntity>> TEST_MOB =
            ENTITY_TYPES.register("test_mob", () ->
                    EntityType.Builder.of(TestMobEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 1.3F)
                            .build("test_mob"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
