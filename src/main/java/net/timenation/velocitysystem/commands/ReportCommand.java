package net.timenation.velocitysystem.commands;

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
import net.kyori.adventure.text.event.ClickEvent;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("report").executes(this::sendReportHelpMessage)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::suggestPlayers).executes(this::sendReportHelpMessage)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hacking").executes(context -> reportPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Hacking"))))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendReportHelpMessage(CommandContext<CommandSource> context) {
        Player player = (Player) context.getSource();

        player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.report"), "velocity.messages.report.info")));
        return 1;
    }

    private int reportPlayer(Player player, UUID target, String reason) {
        if (target == player.getUniqueId()) return 1;
        if (VelocitySystem.getInstance().getProxyServer().getPlayer(target).get().hasPermission("timenation.*")) return 1;

        player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.report"), "velocity.messages.report.successfully", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(target).getPlayersNameWithRankColor(target), reason)));

        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(players -> {
            if(players.hasPermission("timenation.report.see") && TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(player.getUniqueId())) {
                players.sendMessage(Components.parse(I18n.format(players, I18n.format(players, "velocity.prefix.report"), "velocity.messages.report", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(target).getPlayersNameWithRankColor(target), TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(player.getUniqueId()).getPlayersNameWithRankColor(player.getUniqueId()), reason)));
                players.sendMessage(Components.parse(I18n.format(players, "velocity.messages.report.hover")).clickEvent(ClickEvent.runCommand("/join " + VelocitySystem.getInstance().getProxyServer().getPlayer(target).get().getCurrentServer().get().getServerInfo().getName())));
            }
        });

        return 1;
    }

    private CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> suggestionsBuilder.suggest(player.getUsername()));
        return suggestionsBuilder.buildFuture();
    }
}