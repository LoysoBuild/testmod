package com.test_project.items.weapone.pike;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/**
 * Класс пики - наследник меча с поддержкой уникальных механик (например, контратаки).
 */
public class PikeItem extends SwordItem {
    // Длительность окна для контратаки (в тиках, 10 = 0.5 секунды)
    public static final int COUNTERATTACK_WINDOW = 10;

    /**
     * @param tier              Материал (Tier) пики (например, Tiers.IRON)
     * @param properties        Свойства предмета, включая урон и скорость через .attributes(...)
     */
    public PikeItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    // Здесь можно добавить override методов для особых эффектов пики (например, анимация, звук, особая атака)
}
