package net.timenation.velocitysystem.manager;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.ICloudPlayer;
import net.kyori.adventure.text.Component;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.util.concurrent.TimeUnit;

public class TablistManager {

    private int count;

    public TablistManager() {
        this.count = 1;
        VelocitySystem.getInstance().getProxyServer().getEventManager().register(VelocitySystem.getInstance(), this);

        VelocitySystem.getInstance().getProxyServer().getScheduler().buildTask(VelocitySystem.getInstance(), () -> {
            switch (count) {
                case 1 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_two"));
                    VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.sendPlayerListFooter(footerComponent));
                    count++;
                }
                case 2 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_three", CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getOnlinePlayerCount(), CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getMaxPlayers()));
                    VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.sendPlayerListFooter(footerComponent));
                    count++;
                }
                case 3 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_one"));
                    VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> player.sendPlayerListFooter(footerComponent));
                    count = 1;
                }
            }
        }).repeat(10, TimeUnit.SECONDS).schedule();
    }

    @Subscribe
    public void handleLogin(LoginEvent event) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(current -> {
            ICloudPlayer iCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(current.getUniqueId());
            Component headerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.header", iCloudPlayer.getConnectedServerName()));
            switch (count) {
                case 2 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_two"));
                    current.sendPlayerListFooter(footerComponent);
                }
                case 3 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_three", CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getOnlinePlayerCount(), CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getMaxPlayers()));
                    current.sendPlayerListFooter(footerComponent);
                }
                case 1 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_one"));
                    current.sendPlayerListFooter(footerComponent);
                }
            }
            current.sendPlayerListHeader(headerComponent);
        });
    }

    @Subscribe
    public void handleDisconnect(DisconnectEvent event) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(current -> {
            ICloudPlayer iCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(current.getUniqueId());
            Component headerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.header", iCloudPlayer.getConnectedServerName()));
            switch (count) {
                case 2 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_two"));
                    current.sendPlayerListFooter(footerComponent);
                }
                case 3 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_three", CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getOnlinePlayerCount(), CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getMaxPlayers()));
                    current.sendPlayerListFooter(footerComponent);
                }
                case 1 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_one"));
                    current.sendPlayerListFooter(footerComponent);
                }
            }
            current.sendPlayerListHeader(headerComponent);
        });
    }

    @Subscribe
    public void handleServerPostConnect(ServerPostConnectEvent event) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(current -> {
            ICloudPlayer iCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(current.getUniqueId());
            Component headerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.header", iCloudPlayer.getConnectedServerName()));
            switch (count) {
                case 2 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_two"));
                    current.sendPlayerListFooter(footerComponent);
                }
                case 3 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_three", CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getOnlinePlayerCount(), CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy").getMaxPlayers()));
                    current.sendPlayerListFooter(footerComponent);
                }
                case 1 -> {
                    Component footerComponent = Components.parse(VelocitySystem.getInstance().getConfigManager().getString("tablist.footer_one"));
                    current.sendPlayerListFooter(footerComponent);
                }
            }
            current.sendPlayerListHeader(headerComponent);
        });
    }
}