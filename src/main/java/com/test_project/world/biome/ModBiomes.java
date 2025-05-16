package com.test_project.world.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.bus.api.IEventBus;
import com.test_project.MainMod;

public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, MainMod.MOD_ID);

    // Регистрация биома. ВАЖНО: сам биом будет описан в datapack (JSON)!
    public static final DeferredHolder<Biome, Biome> CUSTOM_PLAINS =
            BIOMES.register("custom_plains", () -> null); // null, потому что описание в JSON

    public static final ResourceKey<Biome> CUSTOM_PLAINS_KEY =
            ResourceKey.create(
                    Registries.BIOME,
                    ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "custom_plains")
            );

    public static void register(IEventBus eventBus) {
        BIOMES.register(eventBus);
    }
}
