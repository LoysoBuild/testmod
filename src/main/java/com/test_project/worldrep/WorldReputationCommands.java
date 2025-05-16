package com.test_project.worldrep;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class WorldReputationCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("addworldrep")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    WorldReputation rep = player.getData(ModAttachments.WORLD_REPUTATION.get());
                                    rep.addPoints(amount);
                                    player.sendSystemMessage(Component.literal("Вам добавлено мировой репутации: " + amount));
                                    return 1;
                                })
                        )
        );

        dispatcher.register(
                Commands.literal("removeworldrep")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    WorldReputation rep = player.getData(ModAttachments.WORLD_REPUTATION.get());
                                    rep.trySpendPoints(amount);
                                    player.sendSystemMessage(Component.literal("У вас отнято мировой репутации: " + amount));
                                    return 1;
                                })
                        )
        );

        dispatcher.register(
                Commands.literal("myworldrep")
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            int rep = player.getData(ModAttachments.WORLD_REPUTATION.get()).getPoints();
                            player.sendSystemMessage(Component.literal("Ваша мировая репутация: " + rep));
                            return rep;
                        })
        );
    }
}
