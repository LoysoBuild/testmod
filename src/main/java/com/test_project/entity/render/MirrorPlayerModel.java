package com.test_project.entity.render;


import com.test_project.entity.list.MirrorPlayerEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class MirrorPlayerModel extends HumanoidModel<MirrorPlayerEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("mainmod", "mirror_player"), "main");

    public MirrorPlayerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(MirrorPlayerEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Кастомные анимации в зависимости от стойки
        switch (entity.getCurrentStance()) {
            case ATTACK -> {
                // Более агрессивная поза
                this.rightArm.xRot += -0.2F;
                this.leftArm.xRot += -0.1F;
            }
            case DEFENSE -> {
                // Защитная поза
                this.rightArm.xRot += -0.5F;
                this.leftArm.xRot += -0.3F;
                this.rightArm.yRot += 0.2F;
                this.leftArm.yRot -= 0.2F;
            }
        }
    }
}
