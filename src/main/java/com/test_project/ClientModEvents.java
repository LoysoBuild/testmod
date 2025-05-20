package com.test_project;

import com.test_project.combat.geko.PlayerComboRenderer;
import com.test_project.combat.geko.CustomPlayerRenderer; // Новый класс
import com.test_project.combat.geko.YourModEntities;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientModEvents {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientModEvents.class);

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        LOGGER.info("Starting entity renderers registration");

        try {
            // 1. Регистрация рендера для кастомной сущности (если нужно)
            LOGGER.debug("Registering PlayerComboRenderer for {}",
                    YourModEntities.PLAYER_ANIMATABLE.getId());

            event.registerEntityRenderer(
                    YourModEntities.PLAYER_ANIMATABLE.get(),
                    PlayerComboRenderer::new
            );

            // 2. Замена рендера стандартного игрока
            LOGGER.debug("Replacing default player renderer");

            event.registerEntityRenderer(
                    EntityType.PLAYER, // Важно: EntityType.PLAYER вместо кастомной сущности
                    CustomPlayerRenderer::new
            );

            LOGGER.debug("Renderers registered successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to register renderer: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Client setup completed");
        // Дополнительная клиентская инициализация
    }
}
