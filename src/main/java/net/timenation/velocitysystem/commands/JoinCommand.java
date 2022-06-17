package net.timenation.velocitysystem.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;

public class JoinCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Player player = (Player) invocation.source();
        String[] arguments = invocation.arguments();

        if(player.hasPermission("timenation.team")) {
            CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(player.getUniqueId()).connect(CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(arguments[0]));
            return;
        }

        if(!CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(arguments[0]).getServiceGroup().isInMaintenance() && CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(arguments[0]).getProperty("active_joinme") != null && CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(arguments[0]).getProperty("active_joinme").getValue().equals(true)) {
            CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(player.getUniqueId()).connect(CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(arguments[0]));
        }
    }
}
