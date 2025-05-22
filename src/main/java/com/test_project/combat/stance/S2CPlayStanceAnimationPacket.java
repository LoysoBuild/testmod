package com.test_project.combat.stance;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CPlayStanceAnimationPacket(StanceType stance) implements CustomPacketPayload {
    public static final Type<S2CPlayStanceAnimationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "play_stance_animation"));
    public static final StreamCodec<FriendlyByteBuf, S2CPlayStanceAnimationPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeEnum(packet.stance()),
                    buf -> new S2CPlayStanceAnimationPacket(buf.readEnum(StanceType.class))
            );

    @Override
    public Type<S2CPlayStanceAnimationPacket> type() {
        return TYPE;
    }

    public static void handle(final S2CPlayStanceAnimationPacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // ctx.player() всегда клиентский игрок
            StanceAnimationManager.playStance(ctx.player(), pkt.stance());
        });
    }
}
