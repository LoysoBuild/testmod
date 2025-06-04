package com.test_project.combat.stance;

import com.test_project.combat.parry.C2SActivateParryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkManager {

    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mainmod");

        // Существующие пакеты стоек
        registrar.playToClient(
                S2CPlayStanceAnimationPacket.TYPE,
                S2CPlayStanceAnimationPacket.STREAM_CODEC,
                S2CPlayStanceAnimationPacket::handle
        );

        registrar.playToServer(
                C2SToggleStancePacket.TYPE,
                C2SToggleStancePacket.STREAM_CODEC,
                C2SToggleStancePacket::handle
        );

        // НОВЫЙ: Пакет парирования
        registrar.playToServer(
                C2SActivateParryPacket.TYPE,
                C2SActivateParryPacket.STREAM_CODEC,
                C2SActivateParryPacket::handle
        );

        System.out.println("[NETWORK] Registered all packets including parry");
    }

    public static void sendToPlayer(S2CPlayStanceAnimationPacket packet, ServerPlayer player) {
        player.connection.send(packet);
    }

    public static void sendToServer(C2SActivateParryPacket packet) {
        // Отправка пакета на сервер (реализуется через клиентский код)
    }
}
