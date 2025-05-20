package com.test_project;

import com.test_project.combat.geko.PlayerComboRenderer;

import com.test_project.combat.geko.YourModEntities;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientModEvents {
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(YourModEntities.PLAYER_ANIMATABLE.get(), PlayerComboRenderer::new);
    }
    public static void onClientSetup(FMLClientSetupEvent event) {}
}
