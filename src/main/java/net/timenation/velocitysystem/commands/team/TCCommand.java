package net.timenation.velocitysystem.commands.team;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

public class TCCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("tc")
                .requires(commandSource -> commandSource.hasPermission("timenation.team.teamchat"))
                .executes(context -> sendHelp((Player) context.getSource()))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.greedyString())
                        .executes(this::executeTeamChat))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(Player player) {
        player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.message.help")));
        return 1;
    }

    private int executeTeamChat(CommandContext<CommandSource> context) {
        if (VelocitySystem.getInstance().getTeamChatList().getLoggedPlayers().contains((Player) context.getSource())) {
            VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> {
                if (player.hasPermission("timenation.team.teamchat") && VelocitySystem.getInstance().getTeamChatList().getLoggedPlayers().contains(player)) {
                    player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.message", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(player.getUniqueId()).getPlayersRankAndName(((Player) context.getSource()).getUniqueId()), context.getArgument("message", String.class).replace("&", "§"))));
                }
            });
        } else {
            context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), I18n.format((Player) context.getSource(), "velocity.prefix.teamchat"), "velocity.messages.teamchat.notloggedin")));
        }
        return 1;
    }
}
