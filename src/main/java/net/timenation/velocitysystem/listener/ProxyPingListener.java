package net.timenation.velocitysystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

public class ProxyPingListener {

    @Subscribe
    public void handleProxyPing(ProxyPingEvent event) {
        ServerPing.Builder builder = event.getPing().asBuilder();

        if (VelocitySystem.getInstance().getConfigManager().getBoolean("maintenance")) {
            builder.version(new ServerPing.Version(2, VelocitySystem.getInstance().getConfigManager().getString("maintenance_protocol")));
            builder.description(Components.ofChildren(Components.parse(VelocitySystem.getInstance().getConfigManager().getString("motd.maintenance_line1")),
                    Component.newline(),
                    Components.parse(VelocitySystem.getInstance().getConfigManager().getString("motd.maintenance_line2"))));
            event.setPing(builder.build());
            return;
        }

        builder.description(Components.ofChildren(Components.parse(VelocitySystem.getInstance().getConfigManager().getString("motd.line1")),
                Component.newline(),
                Components.parse(VelocitySystem.getInstance().getConfigManager().getString("motd.line2"))));

        event.setPing(builder.build());
    }
}
