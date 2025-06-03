package com.test_project.entity;

import com.test_project.MainMod;
import com.test_project.entity.render.MirrorPlayerRenderer;
import com.test_project.entity.render.MirrorPlayerModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = MainMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TEST_MOB.get(), TestMobRenderer::new);
    }

    public static class TestMobRenderer extends MobRenderer<TestMobEntity, HumanoidModel<TestMobEntity>> {
        private static final ResourceLocation TEXTURE =
                ResourceLocation.fromNamespaceAndPath("mainmod", "textures/entity/test_mob.png");

        public TestMobRenderer(EntityRendererProvider.Context context) {
            super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(TestMobEntity entity) {
            return TEXTURE;
        }
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.MIRROR_PLAYER.get(), MirrorPlayerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MirrorPlayerModel.LAYER_LOCATION, MirrorPlayerModel::createBodyLayer);
    }
}
