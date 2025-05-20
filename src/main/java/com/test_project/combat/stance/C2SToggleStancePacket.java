package com.test_project.combat.stance;

import com.test_project.combat.combo.CombatEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.test_project.combat.PlayerCombatSettings;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;

public record C2SToggleStancePacket() implements CustomPacketPayload {
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
            Player player = ctx.player();
            if (player instanceof ServerPlayer serverPlayer) {
                PlayerCombatSettings settings = CombatEventHandler.getSettings(serverPlayer);
                // Проверка кулдауна: если кулдаун еще идет, не переключаем стойку
                if (settings.isStanceCooldown()) {
                    serverPlayer.sendSystemMessage(
                            Component.literal("Стойку можно сменить только после кулдауна!")
                    );
                    return;
                }
                StanceType next = settings.getCurrentStance() == StanceType.ATTACK
                        ? StanceType.DEFENSE
                        : StanceType.ATTACK;
                settings.setCurrentStance(next);
                settings.setStanceCooldown(40); // 2 секунды кулдаун (40 тиков)
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                serverPlayer.sendSystemMessage(
                        Component.literal("Стойка переключена: " + (next == StanceType.ATTACK ? "Атака" : "Защита"))
                );
            }
        });
    }
}
