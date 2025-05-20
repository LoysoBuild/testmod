package com.test_project.combat.geko;


import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


public class PlayerAnimatable extends Mob implements GeoEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerAnimatable.class);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PlayerAnimatable(EntityType<? extends PlayerAnimatable> type, Level level) {
        super(type, level);
        LOGGER.info("Created PlayerAnimatable entity at {}", this.blockPosition());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        LOGGER.debug("Accessing AnimatableInstanceCache for {}", this);
        return this.geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        LOGGER.info("Registering animation controllers for {}", this);

        controllers.add(new AnimationController<>(this, "idle_controller", 5, state -> {
            LOGGER.debug("AnimationController tick for {}", this);

            PlayerCombatSettings settings = this.getCapability(CombatCapabilities.PLAYER_COMBAT, null);
            if (settings != null && settings.getCurrentStance() == StanceType.ATTACK) {
                LOGGER.info("Playing 'idle_attack' animation for {}", this);
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle_attack"));
            }

            LOGGER.info("No animation for {}", this);
            return PlayState.STOP;
        }));
    }

    public static AttributeSupplier.Builder createAttributes() {
        LOGGER.info("Creating attributes for PlayerAnimatable");
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0);
    }

    @Override
    public String toString() {
        return String.format("PlayerAnimatable[%d][%s]", this.getId(), this.blockPosition());
    }
}
