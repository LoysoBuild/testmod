package com.test_project.combat.geko;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PlayerComboRenderer extends GeoEntityRenderer<PlayerAnimatable> {
    public PlayerComboRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerComboModel());
    }
}
