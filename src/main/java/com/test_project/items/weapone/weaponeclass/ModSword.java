package com.test_project.items.weapone.weaponeclass;

import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.world.item.Tier;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ModSword extends AbstractWeapon {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModSword(Tier tier, float attackDamage, float attackSpeed, int durability, double attackRange) {
        super(tier, createWeaponAttributes(tier, attackDamage, attackSpeed, attackRange, "sword"), "sword");
        LOGGER.debug("Created ModSword with damage: {}, speed: {}, range: {}", attackDamage, attackSpeed, attackRange);
    }

    /**
     * Мечи универсальны и эффективны в атакующей стойке
     * Переопределяем для специфичного поведения мечей
     */
    @Override
    public float getStanceDamageModifier(StanceType stance) {
        float modifier = super.getStanceDamageModifier(stance);
        LOGGER.debug("ModSword damage modifier for stance {}: {}", stance, modifier);
        return modifier;
    }

    /**
     * Мечи сохраняют хорошую скорость в обеих стойках
     */
    @Override
    public float getStanceSpeedModifier(StanceType stance) {
        float modifier = super.getStanceSpeedModifier(stance);
        LOGGER.debug("ModSword speed modifier for stance {}: {}", stance, modifier);
        return modifier;
    }

    /**
     * Мечи совместимы со всеми стойками
     */
    @Override
    public boolean isCompatibleWithStance(StanceType stance) {
        boolean compatible = super.isCompatibleWithStance(stance);
        LOGGER.debug("ModSword compatibility with stance {}: {}", stance, compatible);
        return compatible;
    }
}
