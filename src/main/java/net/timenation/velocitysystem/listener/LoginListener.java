package net.timenation.velocitysystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

public class LoginListener {

    @Subscribe
    public void handleLogin(LoginEvent event) {
        Player player = event.getPlayer();

        if (VelocitySystem.getInstance().getPunishManager().isBanned(player.getUniqueId())) {
            long current = System.currentTimeMillis();
            long end = VelocitySystem.getInstance().getPunishManager().getBanEnd(player.getUniqueId());
            if (((current < end ? 1 : 0) | (end == -1L ? 1 : 0)) != 0) {
                player.disconnect(Components.parse(I18n.format(player, "velocity.kickscreen.isbanned", (Object) VelocitySystem.getInstance().getPunishManager().getBanReason(player.getUniqueId()), VelocitySystem.getInstance().getPunishManager().getBanReamainingTime(player, player.getUniqueId()))));
            } else {
                VelocitySystem.getInstance().getPunishManager().unbanPlayer(player.getUniqueId());
                VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(players -> {
                    if (TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(players.getUniqueId()) && players.hasPermission("timenation.punish.see")) {
                        players.sendMessage(Components.parse(I18n.format(players, "velocity.messages.punish.teammember.playerwasunbanned", I18n.format(player, "velocity.prefix.punish"))));
                    }
                });
            }
        }

        if (CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").isInMaintenance()) {
            if (!player.hasPermission("timenation.maintenancejoin")) {
                player.disconnect(Components.parse(VelocitySystem.getInstance().getConfigManager().getString("maintenance_message")));
            }
        }

        if (player.hasPermission("timenation.isinteam") && !TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(player.getUniqueId())) {
            player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.notify"), "velocity.messages.teamchat.isloggedout")));
        }
    }
}
