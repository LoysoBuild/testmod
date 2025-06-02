package com.test_project.combat.stance;

import com.test_project.combat.combo.CombatEventHandler;
import com.test_project.combat.PlayerCombatSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            PlayerCombatSettings settings = CombatEventHandler.getSettings(serverPlayer);

            // Проверка кулдауна
            if (settings.isStanceCooldown()) {
                serverPlayer.sendSystemMessage(Component.literal("§c⏰ Стойку можно сменить только после кулдауна!"));
                return;
            }

            // Переключение стойки
            StanceType next = settings.getCurrentStance() == StanceType.ATTACK
                    ? StanceType.DEFENSE
                    : StanceType.ATTACK;

            settings.setCurrentStance(next);
            settings.setStanceCooldown(40); // 2 секунды

            // Эффекты переключения
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));

            // Уведомление игроку
            String stanceName = next == StanceType.ATTACK ? "§c⚔ Атака" : "§9🛡 Защита";
            serverPlayer.sendSystemMessage(Component.literal("§e✨ Стойка переключена: " + stanceName));

            // Смена пресета оружия
            ItemStack stack = serverPlayer.getMainHandItem();
            if (!stack.isEmpty() && WeaponPresetHelper.isWeaponSupported(stack)) {
                WeaponPresetHelper.setPresetForItem(stack, next, serverPlayer);
            }

            // Отправка пакета анимации на клиент
            try {
                NetworkManager.sendToPlayer(new S2CPlayStanceAnimationPacket(next), serverPlayer);
            } catch (Exception e) {
                System.err.println("Failed to send stance animation packet: " + e.getMessage());
            }
        });
    }
}
