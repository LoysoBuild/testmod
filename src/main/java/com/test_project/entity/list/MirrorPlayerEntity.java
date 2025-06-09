package com.test_project.entity.list;

import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import com.test_project.combat.stance.NetworkManager;
import com.test_project.combat.stance.S2CPlayStanceAnimationPacket;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Сущность-зеркало игрока с системой боевых стоек.
 * Демонстрирует работу системы стоек на NPC с анимациями переходов.
 */
public class MirrorPlayerEntity extends PathfinderMob {

    private PlayerCombatSettings combatSettings;
    private int stanceCooldown = 0;
    private int lastStanceChange = 0;

    // КОНСТАНТЫ: Настройки поведения
    private static final int MIN_STANCE_CHANGE_INTERVAL = 100; // 5 секунд
    private static final int MAX_STANCE_CHANGE_INTERVAL = 200; // 10 секунд
    private static final float LOW_HEALTH_THRESHOLD = 0.3f;    // 30% здоровья
    private static final double ANIMATION_RANGE = 256.0;       // 16 блоков

    public MirrorPlayerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.combatSettings = new PlayerCombatSettings();
        this.combatSettings.setCurrentStance(StanceType.ATTACK);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MirrorPlayerAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (stanceCooldown > 0) {
                stanceCooldown--;
            }

            this.updateCombatStance();
            this.combatSettings.tickCooldown();
        }
    }

    private void updateCombatStance() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        // ВРЕМЕННОЕ ПЕРЕКЛЮЧЕНИЕ: Меняем стойку через случайные интервалы
        int timeSinceLastChange = this.tickCount - lastStanceChange;
        int changeInterval = MIN_STANCE_CHANGE_INTERVAL + this.random.nextInt(MAX_STANCE_CHANGE_INTERVAL - MIN_STANCE_CHANGE_INTERVAL);

        if (timeSinceLastChange > changeInterval) {
            this.switchStance();
            lastStanceChange = this.tickCount;
        }

        // ЭКСТРЕННОЕ ПЕРЕКЛЮЧЕНИЕ: Переходим в защиту при низком здоровье
        if (this.getHealth() / this.getMaxHealth() < LOW_HEALTH_THRESHOLD &&
                this.combatSettings.getCurrentStance() == StanceType.ATTACK) {
            this.switchStance();
        }
    }

    /**
     * ИСПРАВЛЕННЫЙ МЕТОД: Переключение стойки с правильной передачей параметров анимации
     */
    public void switchStance() {
        if (stanceCooldown > 0) return;

        // ИСПРАВЛЕНО: Сохраняем текущую стойку ДО изменения
        StanceType currentStance = this.combatSettings.getCurrentStance();
        StanceType newStance = currentStance == StanceType.ATTACK
                ? StanceType.DEFENSE
                : StanceType.ATTACK;

        this.combatSettings.setCurrentStance(newStance);
        this.stanceCooldown = 40;

        if (!this.level().isClientSide) {
            this.level().players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer &&
                        this.distanceToSqr(player) < ANIMATION_RANGE) {
                    // ИСПРАВЛЕНО: Передаем оба параметра - fromStance и toStance
                    NetworkManager.sendToPlayer(new S2CPlayStanceAnimationPacket(currentStance, newStance), serverPlayer);
                }
            });
        }

        String stanceName = newStance == StanceType.ATTACK ? "Attack" : "Defense";
        String colorCode = newStance == StanceType.ATTACK ? "c" : "9";

        this.level().players().forEach(player -> {
            if (this.distanceToSqr(player) < ANIMATION_RANGE) {
                player.sendSystemMessage(Component.literal(
                        "§6Mirror Player switched to §" + colorCode + stanceName + " Stance"
                ));
            }
        });
    }

    public StanceType getCurrentStance() {
        return this.combatSettings.getCurrentStance();
    }

    public PlayerCombatSettings getCombatSettings() {
        return this.combatSettings;
    }

    public boolean canUseWeapon(ItemStack stack) {
        return stack.getItem() instanceof AbstractWeapon;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.switchStance();
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    /**
     * КАСТОМНЫЙ МЕТОД: Атака с модификаторами стойки
     */
    public boolean performStanceAttack(LivingEntity target) {
        // Применяем модификатор урона в зависимости от стойки
        float damageMultiplier = switch (this.getCurrentStance()) {
            case ATTACK -> 1.15f;  // +15% урона в атакующей стойке
            case DEFENSE -> 0.85f; // -15% урона в защитной стойке
        };

        // Получаем базовый урон
        float baseDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float modifiedDamage = baseDamage * damageMultiplier;

        // Создаем источник урона
        DamageSource damageSource = this.damageSources().mobAttack(this);

        // Наносим урон
        boolean success = target.hurt(damageSource, modifiedDamage);

        if (success) {
            // Воспроизводим анимацию удара
            this.swing(InteractionHand.MAIN_HAND);

            // Входим в боевой режим
            this.combatSettings.enterCombat();
        }

        return success;
    }

    public static class MirrorPlayerAttackGoal extends MeleeAttackGoal {
        private final MirrorPlayerEntity mirrorPlayer;

        public MirrorPlayerAttackGoal(MirrorPlayerEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
            this.mirrorPlayer = mob;
        }

        @Override
        public void start() {
            super.start();
            this.mirrorPlayer.combatSettings.enterCombat();
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.mirrorPlayer.getTarget() != null;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.canPerformAttack(target)) {
                this.resetAttackCooldown();
                // Используем наш кастомный метод атаки с модификаторами стойки
                this.mirrorPlayer.performStanceAttack(target);
            }
        }
    }
}
