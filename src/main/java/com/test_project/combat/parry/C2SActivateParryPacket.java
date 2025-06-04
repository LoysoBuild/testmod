package com.test_project.combat.parry;

import com.test_project.combat.parry.ParrySystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;

public record C2SActivateParryPacket() implements CustomPacketPayload {

    public static final Type<C2SActivateParryPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "activate_parry"));

    public static final StreamCodec<FriendlyByteBuf, C2SActivateParryPacket> STREAM_CODEC =
            StreamCodec.unit(new C2SActivateParryPacket());

    @Override
    public Type<C2SActivateParryPacket> type() {
        return TYPE;
    }

    public static void handle(final C2SActivateParryPacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                ParrySystem.activateParry(serverPlayer);
            }
        });
    }
}
