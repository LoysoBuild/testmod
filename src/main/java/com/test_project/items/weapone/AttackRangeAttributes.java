package com.test_project.items.weapone;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AttackRangeAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, "yourmodid");

    public static final DeferredHolder<Attribute, Attribute> ATTACK_RANGE =
            ATTRIBUTES.register("attack_range", () -> new RangedAttribute(
                    "attributes.yourmodid.attack_range",
                    0.0,
                    0.0,
                    16.0
            ));
}
