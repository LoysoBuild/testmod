package com.test_project.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

public class FactionCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("faction")
                        .then(Commands.literal("reputation")
                                .executes(FactionCommands::showReputation)
                        )
                        .then(Commands.literal("addrep")
                                .then(Commands.argument("faction", StringArgumentType.word())
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .executes(FactionCommands::addReputation)
                                        )
                                )
                        )
                        .then(Commands.literal("subrep")
                                .then(Commands.argument("faction", StringArgumentType.word())
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .executes(FactionCommands::subReputation)
                                        )
                                )
                        )
        );
    }

    private static int showReputation(CommandContext<CommandSourceStack> ctx) {
        Player player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Только для игроков!"));
            return 0;
        }

        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data == null) {
            player.sendSystemMessage(Component.literal("Нет данных о фракциях."));
            return 1;
        }

        player.sendSystemMessage(Component.literal("Ваша репутация с фракциями:"));
        for (FactionBase faction : FactionRegistry.all()) {
            int rep = data.getReputation(faction.getId());
            player.sendSystemMessage(Component.literal(
                    "- " + faction.getDisplayName() + " (" + faction.getId() + "): " + rep
            ));
        }
        return 1;
    }

    private static int addReputation(CommandContext<CommandSourceStack> ctx) {
        Player player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Только для игроков!"));
            return 0;
        }

        String factionId = StringArgumentType.getString(ctx, "faction");
        int value = IntegerArgumentType.getInteger(ctx, "value");

        if (FactionRegistry.get(factionId) == null) {
            player.sendSystemMessage(Component.literal("Фракция не найдена: " + factionId));
            return 0;
        }

        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data != null) {
            data.addReputation(factionId, value);
            player.sendSystemMessage(Component.literal(
                    "Ваша репутация с " + factionId + " увеличена на " + value + ". Теперь: " + data.getReputation(factionId)
            ));
        }
        return 1;
    }

    private static int subReputation(CommandContext<CommandSourceStack> ctx) {
        Player player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Только для игроков!"));
            return 0;
        }

        String factionId = StringArgumentType.getString(ctx, "faction");
        int value = IntegerArgumentType.getInteger(ctx, "value");

        if (FactionRegistry.get(factionId) == null) {
            player.sendSystemMessage(Component.literal("Фракция не найдена: " + factionId));
            return 0;
        }

        FactionPlayerData data = player.getData(FactionAttachments.FACTION_DATA.get());
        if (data != null) {
            data.addReputation(factionId, -value);
            player.sendSystemMessage(Component.literal(
                    "Ваша репутация с " + factionId + " уменьшена на " + value + ". Теперь: " + data.getReputation(factionId)
            ));
        }
        return 1;
    }
}
