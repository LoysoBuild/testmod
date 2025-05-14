package com.test_project.worldrep;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "mainmod");

    public static final Supplier<AttachmentType<WorldReputation>> WORLD_REPUTATION =
            ATTACHMENT_TYPES.register(
                    "world_reputation",
                    () -> AttachmentType.builder(WorldReputation::new)
                            .serialize(WorldReputation.CODEC)
                            .copyOnDeath() // копировать при смерти игрока
                            .build()
            );

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
