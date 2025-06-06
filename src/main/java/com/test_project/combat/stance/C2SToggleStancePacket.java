package com.test_project.combat.stance;

import com.test_project.combat.CombatEventHandler;
import com.test_project.combat.PlayerCombatSettings;
import com.test_project.items.weapone.preset.WeaponPresetManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public record C2SToggleStancePacket() implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<C2SToggleStancePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mainmod", "toggle_stance"));

    public static final StreamCodec<FriendlyByteBuf, C2SToggleStancePacket> STREAM_CODEC =
            StreamCodec.unit(new C2SToggleStancePacket());

    @Override
    public Type<C2SToggleStancePacket> type() {
        return TYPE;
    }

    public static void handle(final C2SToggleStancePacket pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer serverPlayer)) {
                LOGGER.warn("[SERVER] Received stance toggle from non-server player");
                return;
            }

            PlayerCombatSettings settings = CombatEventHandler.getSettings(serverPlayer);
            StanceType currentStance = settings.getCurrentStance();

            LOGGER.info("[SERVER] Processing stance toggle for player: {} (current: {})",
                    serverPlayer.getName().getString(), currentStance);

            // Проверка кулдауна
            if (settings.isStanceCooldown()) {
                serverPlayer.sendSystemMessage(Component.literal("§cStance is on cooldown!"));
                LOGGER.debug("[SERVER] Stance toggle blocked - cooldown active for player: {}",
                        serverPlayer.getName().getString());
                return;
            }

            // Переключение стойки
            StanceType nextStance = currentStance == StanceType.ATTACK ?
                    StanceType.DEFENSE : StanceType.ATTACK;

            LOGGER.info("[SERVER] Switching stance: {} -> {} for player: {}",
                    currentStance, nextStance, serverPlayer.getName().getString());

            settings.setCurrentStance(nextStance);
            settings.setStanceCooldown(40); // 2 секунды кулдауна

            // Применяем пресет при смене стойки
            ItemStack mainHand = serverPlayer.getMainHandItem();
            WeaponPresetManager.changeWeaponPreset(serverPlayer, mainHand, nextStance);

            // Уведомление игрока
            String stanceName = nextStance == StanceType.ATTACK ? "§cAttack" : "§9Defense";
            serverPlayer.sendSystemMessage(Component.literal("§eStance: " + stanceName));

            // ИСПРАВЛЕНО: Добираем детальное логирование отправки пакета
            LOGGER.info("[SERVER] Sending animation packet for stance: {} to player: {}",
                    nextStance, serverPlayer.getName().getString());

            try {
                NetworkManager.sendToPlayer(new S2CPlayStanceAnimationPacket(nextStance), serverPlayer);
                LOGGER.debug("[SERVER] Successfully sent animation packet for stance: {}", nextStance);
            } catch (Exception e) {
                LOGGER.error("[SERVER] Failed to send animation packet: {}", e.getMessage(), e);
            }

            LOGGER.info("[SERVER] Stance change completed: {} for player: {}",
                    nextStance, serverPlayer.getName().getString());
        });
    }
}
