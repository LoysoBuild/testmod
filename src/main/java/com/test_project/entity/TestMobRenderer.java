package com.test_project.entity;


import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TestMobRenderer extends MobRenderer<TestMobEntity, HumanoidModel<TestMobEntity>> {
    public TestMobRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TestMobEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("mainmod", "textures/entity/test_mob.png");
    }
}