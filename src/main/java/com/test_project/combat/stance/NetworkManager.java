package com.test_project.combat.stance;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class NetworkManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mainmod");

        // Регистрация пакетов анимации
        registrar.playToClient(
                S2CPlayStanceAnimationPacket.TYPE,
                S2CPlayStanceAnimationPacket.STREAM_CODEC,
                S2CPlayStanceAnimationPacket::handle
        );

        // ДОБАВЛЕНО: Регистрация пакета остановки анимации
        registrar.playToClient(
                S2CStopStanceAnimationPacket.TYPE,
                S2CStopStanceAnimationPacket.STREAM_CODEC,
                S2CStopStanceAnimationPacket::handle
        );

        registrar.playToServer(
                C2SToggleStancePacket.TYPE,
                C2SToggleStancePacket.STREAM_CODEC,
                C2SToggleStancePacket::handle
        );

        LOGGER.info("[NETWORK] Registered stance system packets (3 total)");
    }

    public static void sendToServer(C2SToggleStancePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(S2CPlayStanceAnimationPacket packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    // ДОБАВЛЕНО: Метод для отправки пакета остановки анимации
    public static void sendStopAnimationToPlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new S2CStopStanceAnimationPacket());
        LOGGER.debug("[NETWORK] Sent stop animation packet to player: {}", player.getName().getString());
    }
}
