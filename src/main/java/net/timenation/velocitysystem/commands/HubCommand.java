package net.timenation.velocitysystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.ICloudPlayer;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

public class HubCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();
        ICloudPlayer iCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(player.getUniqueId());

        if (iCloudPlayer.getConnectedServerName().startsWith("Lobby-")) {
            player.sendMessage(Components.parse(I18n.format(player, VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.cloud.alreadyconnetedwithlobby")));
        } else {
            iCloudPlayer.sendToLobby();
            player.sendMessage(Components.parse(I18n.format(player, VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.cloud.sendplayertolobby")));
        }
    }
}