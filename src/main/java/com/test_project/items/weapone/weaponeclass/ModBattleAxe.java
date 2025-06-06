package com.test_project.items.weapone.weaponeclass;

import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.world.item.Tier;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ModBattleAxe extends AbstractWeapon {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ModBattleAxe(Tier tier, float attackDamage, float attackSpeed, int durability, double attackRange) {
        super(tier, createWeaponAttributes(tier, attackDamage, attackSpeed, attackRange, "axe"), "axe");
        LOGGER.debug("Created ModBattleAxe with damage: {}, speed: {}, range: {}", attackDamage, attackSpeed, attackRange);
    }

    /**
     * Топоры превосходны в атакующей стойке, но менее эффективны в защитной
     * Переопределяем для специфичного поведения топоров
     */
    @Override
    public float getStanceDamageModifier(StanceType stance) {
        float modifier = super.getStanceDamageModifier(stance);
        LOGGER.debug("ModBattleAxe damage modifier for stance {}: {}", stance, modifier);
        return modifier;
    }

    /**
     * Топоры медленнее из-за веса, особенно в защитной стойке
     */
    @Override
    public float getStanceSpeedModifier(StanceType stance) {
        float modifier = super.getStanceSpeedModifier(stance);
        LOGGER.debug("ModBattleAxe speed modifier for stance {}: {}", stance, modifier);
        return modifier;
    }

    /**
     * Топоры имеют увеличенную дальность атаки
     */
    @Override
    public boolean isCompatibleWithStance(StanceType stance) {
        boolean compatible = super.isCompatibleWithStance(stance);
        LOGGER.debug("ModBattleAxe compatibility with stance {}: {}", stance, compatible);
        return compatible;
    }
}
