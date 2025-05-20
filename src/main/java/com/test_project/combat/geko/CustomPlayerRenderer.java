package com.test_project.combat.geko;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CustomPlayerRenderer extends GeoEntityRenderer<AbstractClientPlayer> {
    public CustomPlayerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerComboModel());
    }
}
