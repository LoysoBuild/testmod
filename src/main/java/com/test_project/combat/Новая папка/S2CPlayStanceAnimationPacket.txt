package com.test_project.combat.stance;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.client.player.LocalPlayer;

public record S2CPlayStanceAnimationPacket(StanceType stance) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CPlayStanceAnimationPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "play_stance_animation"));

    // ИСПРАВЛЕНИЕ: Теперь используем IntFunction и ToIntFunction
    public static final StreamCodec<FriendlyByteBuf, S2CPlayStanceAnimationPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(StanceType.BY_ID, StanceType::getId),
                    S2CPlayStanceAnimationPacket::stance,
                    S2CPlayStanceAnimationPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final S2CPlayStanceAnimationPacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof LocalPlayer clientPlayer) {
                System.out.println("[CLIENT] Playing stance animation: " + pkt.stance());
                StanceAnimationManager.playStance(clientPlayer, pkt.stance());
            }
        });
    }
}
