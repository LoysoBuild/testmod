package com.test_project.items;


import com.test_project.combat.PlayerCombatSettings;
import com.test_project.combat.combo.CombatEventHandler;
import com.test_project.combat.stance.StanceType;
import com.test_project.items.weapone.AbstractWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EquipmentEventHandler {
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;

        ItemStack newMain = event.getTo();
        if (newMain.getItem() instanceof AbstractWeapon weapon) {
            PlayerCombatSettings settings = CombatEventHandler.getSettings(player);
            String comboId = weapon.getDefaultComboId();

            // Устанавливаем дефолтные комбо только если они не заданы
            if (settings.getCombo(StanceType.ATTACK) == null) {
                settings.setCombo(StanceType.ATTACK, comboId);
            }
            if (settings.getCombo(StanceType.DEFENSE) == null) {
                settings.setCombo(StanceType.DEFENSE, comboId);
            }

            // Сбрасываем комбо при смене оружия
            settings.resetCombo();
            settings.setLastUsedWeaponType(weapon.getClass().getSimpleName());
        }
    }
}
