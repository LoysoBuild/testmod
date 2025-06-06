package com.test_project.combat.stance;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public record S2CPlayStanceAnimationPacket(StanceType stance) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<S2CPlayStanceAnimationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "play_stance_animation"));

    public static final StreamCodec<FriendlyByteBuf, S2CPlayStanceAnimationPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeEnum(pkt.stance);
                        LOGGER.debug("[NETWORK] Encoding stance animation packet: {}", pkt.stance);
                    },
                    buf -> {
                        StanceType stance = buf.readEnum(StanceType.class);
                        LOGGER.debug("[NETWORK] Decoding stance animation packet: {}", stance);
                        return new S2CPlayStanceAnimationPacket(stance);
                    }
            );

    @Override
    public Type<S2CPlayStanceAnimationPacket> type() {
        return TYPE;
    }

    public static void handle(final S2CPlayStanceAnimationPacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            LOGGER.info("[CLIENT] Received stance animation packet: {}", pkt.stance());

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                // ИСПРАВЛЕНО: Добавляем приоритет для выполнения на главном потоке
                mc.executeBlocking(() -> {
                    try {
                        StanceAnimationManager.playStance(mc.player, pkt.stance());
                        LOGGER.info("[CLIENT] Successfully processed stance animation: {}", pkt.stance());
                    } catch (Exception e) {
                        LOGGER.error("[CLIENT] Error playing stance animation: {}", e.getMessage(), e);
                    }
                });
            } else {
                LOGGER.warn("[CLIENT] Cannot play stance animation - player is null");
            }
        });
    }
}
