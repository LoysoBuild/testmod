package com.test_project.items;


import com.test_project.items.weapone.weaponeclass.ModSword;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EquipmentEventHandler {

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.MAINHAND) return;

        ItemStack newMain = event.getTo();
        ModSword.handleEquip(player, newMain);
    }
}
