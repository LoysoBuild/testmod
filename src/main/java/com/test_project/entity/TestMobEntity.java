package com.test_project.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Наследуемся от PathfinderMob (универсальный класс для большинства пассивных и нейтральных мобов)
public class TestMobEntity extends PathfinderMob {

    public TestMobEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        // Плавает, если в воде
        this.goalSelector.addGoal(1, new FloatGoal(this));
        // Гуляет по миру
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0, 1));
        // Смотрит на ближайшего игрока
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    // Атрибуты сущности (здоровье, скорость и т.д.)
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    // Если нужен особый тип (например, нежить), раскомментируй:
    // @Override
    // public MobType getMobType() {
    //     return MobType.UNDEAD;
    // }
}
