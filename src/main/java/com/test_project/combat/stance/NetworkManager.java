package com.test_project.combat.stance;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkManager {

    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mainmod");

        // Регистрация пакета для клиента (анимации стоек)
        registrar.playToClient(
                S2CPlayStanceAnimationPacket.TYPE,
                S2CPlayStanceAnimationPacket.STREAM_CODEC,
                S2CPlayStanceAnimationPacket::handle
        );

        // Регистрация пакета для сервера (переключение стоек)
        registrar.playToServer(
                C2SToggleStancePacket.TYPE,
                C2SToggleStancePacket.STREAM_CODEC,
                C2SToggleStancePacket::handle
        );

        System.out.println("[NETWORK] Registered stance packets successfully");
    }

    public static void sendToPlayer(S2CPlayStanceAnimationPacket packet, ServerPlayer player) {
        player.connection.send(packet);
    }
}
