package net.timenation.velocitysystem.commands.team;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;

public class NotificationCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("notify")
                .requires(commandSource -> commandSource.hasPermission("timenation.team.notification"))
                .executes(this::sendHelp)
                .then(LiteralArgumentBuilder.<CommandSource>literal("on")
                        .executes(context -> setNotificationStatus(context, true)))
                .then(LiteralArgumentBuilder.<CommandSource>literal("off")
                        .executes(context -> setNotificationStatus(context, false)))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), I18n.format((Player) context.getSource(), "velocity.prefix.notify"), "velocity.messages.teamchat.notify.help")));
        return 1;
    }

    private int setNotificationStatus(CommandContext<CommandSource> context, boolean value) {
        if (value) {
            TimeVelocityAPI.getInstance().getNotificationManager().enableNotifications(((Player) context.getSource()).getUniqueId());
            context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), I18n.format((Player) context.getSource(), "velocity.prefix.notify"), "velocity.messages.teamchat.loggedin")));
        } else {
            TimeVelocityAPI.getInstance().getNotificationManager().disableNotifications(((Player) context.getSource()).getUniqueId());
            context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), I18n.format((Player) context.getSource(), "velocity.prefix.notify"), "velocity.messages.teamchat.loggedout")));
        }
        return 1;
    }
}
