package com.test_project.combat.geko;


import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.stance.StanceType;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerAttackIdleLayer extends RenderLayer<Player, PlayerModel<Player>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerAttackIdleLayer.class);
    private final PlayerModel<Player> model;

    public PlayerAttackIdleLayer(RenderLayerParent<Player, PlayerModel<Player>> parent,
                                 EntityModelSet models,
                                 ModelLayerLocation layerLocation) {
        super(parent);
        this.model = new PlayerModel<>(models.bakeLayer(layerLocation), false);
        LOGGER.info("Initialized attack idle layer for {}", layerLocation);
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       Player player,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTick,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {

        PlayerCombatSettings settings = player.getCapability(CombatCapabilities.PLAYER_COMBAT, null);
        if (settings == null || settings.getCurrentStance() != StanceType.ATTACK) {
            LOGGER.debug("Skipping attack stance layer for {}", player.getName().getString());
            return;
        }

        try {
            LOGGER.debug("Rendering attack stance for {}", player.getName().getString());

            this.model.renderToBuffer(
                    poseStack,
                    buffer.getBuffer(this.model.renderType(
                            ResourceLocation.fromNamespaceAndPath("mainmod", "textures/entity/player_combo.png")
                    )),
                    packedLight,
                    0, // overlayCoords
                    0xFFFFFFFF // ARGB цвет (белый с полной альфой)
            );
        } catch (Exception e) {
            LOGGER.error("Error rendering attack stance: {}", e.getMessage(), e);
        }
    }
}
