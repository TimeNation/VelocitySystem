package net.timenation.velocitysystem.commands.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.property.IPropertyMap;
import eu.thesimplecloud.api.service.ICloudService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ShutdownCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("shutdown").requires(commandSource -> commandSource.hasPermission("velocitysystem.end")).executes(this::sendHelpMessage)
                .then(LiteralArgumentBuilder.<CommandSource>literal("proxy")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("instant")
                                .executes(this::executeInstantShutdownProxy))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("after")
                                .executes(this::sendHelpMessage)
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("secounds", StringArgumentType.string())
                                        .suggests(this::suggestAfterSecounds)
                                        .executes(context -> executeAfterShutdownProxy(context, Integer.parseInt(context.getArgument("secounds", String.class)))))))
                .then(LiteralArgumentBuilder.<CommandSource>literal("network")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("instant")
                                .executes(this::executeInstantShutdownNetwork))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("after")
                                .executes(this::sendHelpMessage)
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("secounds", StringArgumentType.string())
                                        .suggests(this::suggestAfterSecounds)
                                        .executes(context -> executeAfterShutdownNetwork(context, Integer.parseInt(context.getArgument("secounds", String.class)))))))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelpMessage(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.shutdown.help")));
        return 1;
    }

    private CompletableFuture<Suggestions> suggestAfterSecounds(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        suggestionsBuilder.suggest(5);
        suggestionsBuilder.suggest(10);
        suggestionsBuilder.suggest(20);
        suggestionsBuilder.suggest(30);
        return suggestionsBuilder.buildFuture();
    }

    private int executeInstantShutdownProxy(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), "velocity.messages.shutdown")));

        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.disconnect(Components.parse(I18n.format(player, "velocity.screen.shutdown.proxy"))));
        VelocitySystem.getInstance().getProxyServer().shutdown();
        return 1;
    }

    private int executeInstantShutdownNetwork(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), "velocity.messages.shutdown")));

        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.disconnect(Components.parse(I18n.format(player, "velocity.screen.shutdown.network"))));
        CloudAPI.getInstance().getCloudServiceManager().getAllCachedObjects().forEach(iCloudService -> iCloudService.shutdown());
        return 1;
    }

    private int executeAfterShutdownProxy(CommandContext<CommandSource> context, int secounds) {
        if(secounds != 5 && secounds != 10 && secounds != 20 && secounds != 30) {
            context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.shutdown.invaild")));
            return 0;
        }

        AtomicInteger taskSecounds = new AtomicInteger(secounds);
        VelocitySystem.getInstance().getProxyServer().getScheduler().buildTask(VelocitySystem.getInstance(), () -> {
            switch (taskSecounds.get()) {
                case 30, 25, 20, 15, 10, 5, 4, 3, 2, 1, 0 -> VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.showTitle(Title.title(Component.text("§c§l" + taskSecounds.get()), Components.parse(I18n.format(player, "velocity.title.shutdown.proxy", taskSecounds)))));
                case -1 -> {
                    VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.disconnect(Components.parse(I18n.format(player, "velocity.screen.shutdown.proxy"))));
                    VelocitySystem.getInstance().getProxyServer().shutdown();
                }
            }

            taskSecounds.getAndDecrement();
        }).repeat(1, TimeUnit.SECONDS).schedule();
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.shutdown.proxy")));

        return 1;
    }

    private int executeAfterShutdownNetwork(CommandContext<CommandSource> context, int secounds) {
        if(secounds != 5 && secounds != 10 && secounds != 20 && secounds != 30) {
            context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.shutdown.invaild")));
            return 0;
        }

        AtomicInteger taskSecounds = new AtomicInteger(secounds);
        VelocitySystem.getInstance().getProxyServer().getScheduler().buildTask(VelocitySystem.getInstance(), () -> {
            switch (taskSecounds.get()) {
                case 30, 25, 20, 15, 10, 5, 4, 3, 2, 1, 0 -> VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.showTitle(Title.title(Component.text("§c§l" + taskSecounds.get()), Components.parse(I18n.format(player, "velocity.title.shutdown.network", taskSecounds)))));
                case -1 -> {
                    VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.disconnect(Components.parse(I18n.format(player,  "velocity.screen.shutdown.network"))));
                    CloudAPI.getInstance().getCloudServiceManager().getAllCachedObjects().forEach(iCloudService -> iCloudService.shutdown());
                }
            }

            taskSecounds.getAndDecrement();
        }).repeat(1, TimeUnit.SECONDS).schedule();
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.shutdown.network")));

        return 1;
    }
}