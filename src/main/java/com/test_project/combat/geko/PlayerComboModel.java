package com.test_project.combat.geko;

import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

public class PlayerComboModel extends GeoModel<PlayerAnimatable> {
    @Override
    public ResourceLocation getModelResource(PlayerAnimatable entity) {
        return ResourceLocation.fromNamespaceAndPath("mainmod", "geo/player_combo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlayerAnimatable entity) {
        return ResourceLocation.fromNamespaceAndPath("mainmod", "textures/entity/player_combo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlayerAnimatable entity) {
        return ResourceLocation.fromNamespaceAndPath("mainmod", "animations/player_combo.animation.json");
    }
}
