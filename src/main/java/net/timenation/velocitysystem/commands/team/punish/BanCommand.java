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

public class BanCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("ban").requires(commandSource -> commandSource.hasPermission("timenation.team.punish")).executes(BanCommand::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string()).suggests(this::setPlayersSuggestions).executes(BanCommand::sendHelp)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hacking").executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Hacking", 604800))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Drohungen").executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Drohungen", 2419000))))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Cape/Skin/Name").executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Cape/Skin/Name", 2419000)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Teaming").executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Teaming", 604800)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hacking-Bestätigung").requires(commandSource -> commandSource.hasPermission("timenation.punish.higher")).executes(context -> overriteBan((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Hacking", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Todeswunsch").requires(commandSource -> commandSource.hasPermission("timenation.punish.higher")).executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Todeswunsch", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Bannumgehung").requires(commandSource -> commandSource.hasPermission("timenation.punish.higher")).executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Bannumgehung", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Diskriminierung").requires(commandSource -> commandSource.hasPermission("timenation.punish.higher")).executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Diskriminierung", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Sicherheitsban").requires(commandSource -> PermissionPool.getInstance().getPermissionPlayerManager().getCachedPermissionPlayer(((Player) commandSource).getUniqueId()).hasPermissionGroup("Owner")).executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Sicherheitsban", -1)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("Hausverbot").requires(commandSource -> PermissionPool.getInstance().getPermissionPlayerManager().getCachedPermissionPlayer(((Player) commandSource).getUniqueId()).hasPermissionGroup("Owner")).executes(context -> banPlayer((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)), "Hausverbot", -1)))
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
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), getPunishPrefix((Player) context.getSource()), "velocity.messages.punish.info", getPunishPrefix((Player) context.getSource()), TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(((Player) context.getSource()).getUniqueId()).getPlayersRankAndName(((Player) context.getSource()).getUniqueId()))));
        return 1;
    }

    private static String getPunishPrefix(Player player) {
        return I18n.format(player, "velocity.prefix.punish");
    }

    private static int banPlayer(Player player, UUID target, String reason, int duration) {
        if (player.getUniqueId() == target) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.message.punish.cantbanyourself")));
            return 1;
        }

        if (VelocitySystem.getInstance().getPunishManager().isBanned(target)) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.message.punish.isalreadybanned")));
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
        VelocitySystem.getInstance().getPunishManager().banPlayer(target, TimeVelocityAPI.getInstance().getUuidFetcher().getName(target), reason, duration, player);
        if (targetPlayer != null) {
            targetPlayer.disconnect(Components.parse(I18n.format(player, "", "velocity.kickscreen.isbanned", "<dark_red><bold>" + reason, VelocitySystem.getInstance().getPunishManager().getBanReamainingTime(targetPlayer, duration))));
        }

        return 1;
    }

    private static int overriteBan(Player player, UUID target, String reason, int duration) {
        VelocitySystem.getInstance().getPunishManager().unbanPlayer(target, player);
        banPlayer(player, target, reason, duration);

        return 1;
    }
}
