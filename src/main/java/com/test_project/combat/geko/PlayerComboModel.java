package com.test_project.combat.geko;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.model.GeoModel;

public class PlayerComboModel extends GeoModel<PlayerAnimatable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerComboModel.class);

    @Override
    public ResourceLocation getModelResource(PlayerAnimatable animatable) {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath("mainmod", "geo/player_combo.geo.json");
        LOGGER.debug("Loading model resource: {}", path);
        return path;
    }

    @Override
    public ResourceLocation getTextureResource(PlayerAnimatable animatable) {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath("mainmod", "textures/entity/player_combo.png");
        LOGGER.debug("Loading texture resource: {}", path);
        return path;
    }

    @Override
    public ResourceLocation getAnimationResource(PlayerAnimatable animatable) {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath("mainmod", "animations/player_combo.animation.json");
        LOGGER.info("Attempting to load animation resource: {}", path);
        return path;
    }
}
