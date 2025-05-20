package com.test_project.combat.geko;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public class PlayerAttackIdleLayer extends RenderLayer<Player, PlayerModel<Player>> {
    private final PlayerModel<Player> model;

    public PlayerAttackIdleLayer(RenderLayerParent<Player, PlayerModel<Player>> parent, EntityModelSet models, ModelLayerLocation layerLocation) {
        super(parent);
        this.model = new PlayerModel<>(models.bakeLayer(layerLocation), false);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Player player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // Здесь можно вызывать GeckoLib-анимацию или кастомный рендер для idle атакующей стойки
    }
}
