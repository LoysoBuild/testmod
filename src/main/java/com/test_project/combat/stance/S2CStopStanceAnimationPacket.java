package com.test_project.combat.stance;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public record S2CStopStanceAnimationPacket() implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<S2CStopStanceAnimationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "stop_stance_animation"));

    public static final StreamCodec<FriendlyByteBuf, S2CStopStanceAnimationPacket> STREAM_CODEC =
            StreamCodec.unit(new S2CStopStanceAnimationPacket());

    @Override
    public Type<S2CStopStanceAnimationPacket> type() {
        return TYPE;
    }

    public static void handle(final S2CStopStanceAnimationPacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            LOGGER.info("[CLIENT] Received stop stance animation packet");
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                StanceAnimationManager.stopAnimation(mc.player);
                LOGGER.info("[CLIENT] Stopped stance animation for player: {}", mc.player.getName().getString());
            } else {
                LOGGER.warn("[CLIENT] Cannot stop stance animation - player is null");
            }
        });
    }
}
