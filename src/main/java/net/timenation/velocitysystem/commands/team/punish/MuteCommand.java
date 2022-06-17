package net.timenation.velocitysystem.commands.team.punish;

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
import eu.thesimplecloud.module.permission.PermissionPool;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MuteCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("ban").requires(commandSource -> commandSource.hasPermission("timenation.team.punish")).executes(MuteCommand::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::setPlayersSuggestions).executes(MuteCommand::sendHelp)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Werbung").executes(context -> mutePlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Werbung", 1210000)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Beleidigungen").executes(context -> mutePlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Beleidigungen", 1210000)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Verhalten").executes(context -> mutePlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Verhalten", 604800)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Spamming").executes(context -> mutePlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Spamming", 604800)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Server-Beleidigungen").executes(context -> mutePlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Server Beleidigungen", 2419000)))
                ).build();

        return new BrigadierCommand(node);
    }

    private CompletableFuture<Suggestions> setPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> {
            suggestionsBuilder.suggest(player.getUsername());
        });
        return suggestionsBuilder.buildFuture();
    }

    private static int sendHelp(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), getPunishPrefix((Player) context.getSource()), "velocity.messages.mute.info", getPunishPrefix((Player) context.getSource()), TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(((Player) context.getSource()).getUniqueId()).getPlayersRankAndName(((Player) context.getSource()).getUniqueId()))));
        return 1;
    }

    private static String getPunishPrefix(Player player) {
        return I18n.format(player, "velocity.prefix.punish");
    }

    private static int mutePlayer(Player player, UUID target, String reason, int duration) {
        if (VelocitySystem.getInstance().getPunishManager().ismuted(target) || VelocitySystem.getInstance().getPunishManager().isBanned(target)) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.message.punish.isalreadymuted")));
            return 1;
        }

        if (PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(target).getBlockingOrNull().hasPermissionGroup("Owner") && !PermissionPool.getInstance().getPermissionPlayerManager().getCachedPermissionPlayer(player.getUniqueId()).hasPermissionGroup("Owner")) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.message.punish.cantbanplayer")));
            return 1;
        }

        if (PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(target).getBlockingOrNull().hasPermission("timenation.team.punish") && !PermissionPool.getInstance().getPermissionPlayerManager().getCachedPermissionPlayer(player.getUniqueId()).hasPermissionGroup("Owner")) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.message.punish.cantbanplayer")));
            return 1;
        }

        Player targetPlayer = VelocitySystem.getInstance().getProxyServer().getPlayer(target).orElseGet(new Supplier<Player>() {
            @Override
            public Player get() {
                return null;
            }
        });
        VelocitySystem.getInstance().getPunishManager().mutePlayer(target, TimeVelocityAPI.getInstance().getUuidFetcher().getName(target), reason, duration, player);
        if (targetPlayer != null) {
            targetPlayer.sendMessage(Components.parse(I18n.format(targetPlayer, getPunishPrefix(targetPlayer), "velocity.messages.punish.wasmuted", VelocitySystem.getInstance().getPunishManager().getMuteReason(target))));
        }

        return 1;
    }
}
