package com.test_project.entity.render;

import com.test_project.MainMod;


import com.test_project.entity.list.MirrorPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MirrorPlayerRenderer extends MobRenderer<MirrorPlayerEntity, MirrorPlayerModel> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "textures/entity/mirror_player.png");

    public MirrorPlayerRenderer(EntityRendererProvider.Context context) {
        super(context, new MirrorPlayerModel(context.bakeLayer(MirrorPlayerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MirrorPlayerEntity entity) {
        return TEXTURE;
    }
}
