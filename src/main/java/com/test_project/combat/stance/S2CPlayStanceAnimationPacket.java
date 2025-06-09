package com.test_project.combat.stance;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Сетевой пакет для передачи информации о смене стойки с сервера на клиент.
 * Содержит информацию об исходной и целевой стойке для правильной анимации перехода.
 */
public record S2CPlayStanceAnimationPacket(StanceType fromStance, StanceType toStance) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<S2CPlayStanceAnimationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "play_stance_animation"));

    /**
     * КОДЕК: Сериализация и десериализация пакета
     * Передает два enum'а для определения направления перехода
     */
    public static final StreamCodec<FriendlyByteBuf, S2CPlayStanceAnimationPacket> STREAM_CODEC =
            StreamCodec.of(
                    // Сериализация: записываем fromStance и toStance
                    (buf, pkt) -> {
                        buf.writeEnum(pkt.fromStance);
                        buf.writeEnum(pkt.toStance);
                        LOGGER.debug("[NETWORK] Encoded stance transition: {} -> {}", pkt.fromStance, pkt.toStance);
                    },
                    // Десериализация: читаем fromStance и toStance
                    buf -> {
                        StanceType fromStance = buf.readEnum(StanceType.class);
                        StanceType toStance = buf.readEnum(StanceType.class);
                        LOGGER.debug("[NETWORK] Decoded stance transition: {} -> {}", fromStance, toStance);
                        return new S2CPlayStanceAnimationPacket(fromStance, toStance);
                    }
            );

    @Override
    public Type<S2CPlayStanceAnimationPacket> type() {
        return TYPE;
    }

    /**
     * ОБРАБОТЧИК: Обработка пакета на клиенте
     * Запускает анимацию перехода между стойками
     */
    public static void handle(final S2CPlayStanceAnimationPacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            LOGGER.info("[CLIENT] Received stance transition packet: {} -> {}", pkt.fromStance(), pkt.toStance());

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                // ОПТИМИЗАЦИЯ: Выполняем на главном потоке клиента
                mc.execute(() -> {
                    try {
                        // ИСПРАВЛЕНО: Вызываем метод перехода для анимации переключения
                        StanceAnimationManager.playStanceTransition(mc.player, pkt.fromStance(), pkt.toStance());
                        LOGGER.info("[CLIENT] Successfully processed stance transition: {} -> {}",
                                pkt.fromStance(), pkt.toStance());
                    } catch (Exception e) {
                        LOGGER.error("[CLIENT] Error playing stance transition: {}", e.getMessage(), e);

                        // FALLBACK: Если анимация перехода не удалась, переходим к idle-анимации
                        try {
                            StanceAnimationManager.playStance(mc.player, pkt.toStance());
                            LOGGER.info("[CLIENT] Fallback to idle stance: {}", pkt.toStance());
                        } catch (Exception fallbackException) {
                            LOGGER.error("[CLIENT] Fallback also failed: {}", fallbackException.getMessage());
                        }
                    }
                });
            } else {
                LOGGER.warn("[CLIENT] Cannot play stance transition - player is null");
            }
        });
    }
}
