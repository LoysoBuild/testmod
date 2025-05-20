package com.test_project.combat.geko;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlayerComboRenderer extends GeoEntityRenderer<PlayerAnimatable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerComboRenderer.class);

    public PlayerComboRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerComboModel());
        LOGGER.info("PlayerComboRenderer initialized");
    }
}
