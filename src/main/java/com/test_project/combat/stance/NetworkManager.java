package com.test_project.combat.stance;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkManager {
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mainmod");
        registrar.playToClient(
                S2CPlayStanceAnimationPacket.TYPE,
                S2CPlayStanceAnimationPacket.STREAM_CODEC,
                S2CPlayStanceAnimationPacket::handle
        );
    }

    public static void sendToPlayer(S2CPlayStanceAnimationPacket packet, ServerPlayer player) {
        player.connection.send(packet);
    }
}
