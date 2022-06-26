package net.timenation.velocitysystem.commands.team;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.util.ArrayList;
import java.util.List;

public class TeamChatCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("teamchat")
                .requires(commandSource -> commandSource.hasPermission("timenation.team.teamchat"))
                .executes(context -> sendHelp((Player) context.getSource()))
                .then(LiteralArgumentBuilder.<CommandSource>literal("login").executes(this::login))
                .then(LiteralArgumentBuilder.<CommandSource>literal("logout").executes(this::logout))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(Player player) {
        player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.help")));
        return 1;
    }

    private int login(CommandContext<CommandSource> context) {
        Player player = (Player) context.getSource();
        
        if (!VelocitySystem.getInstance().getTeamChatList().getLoggedPlayers().contains(player)) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.loggedin")));
            VelocitySystem.getInstance().getTeamChatList().getLoggedPlayers().add(player);
        } else {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.alreadyloggedin")));
        }
        return 1;
    }

    private int logout(CommandContext<CommandSource> context) {
        Player player = (Player) context.getSource();

        if (VelocitySystem.getInstance().getTeamChatList().getLoggedPlayers().contains(player)) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.loggedout")));
            VelocitySystem.getInstance().getTeamChatList().getLoggedPlayers().remove(player);
        } else {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.teamchat"), "velocity.messages.teamchat.loggedout")));
        }
        return 1;
    }
}