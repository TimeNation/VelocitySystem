package net.timenation.velocitysystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

public class PingCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();

        player.sendMessage(Components.parse(I18n.format(player, VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.ping", player.getPing())));
    }
}