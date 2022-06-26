package net.timenation.velocitysystem.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.io.IOException;

public class ToggleMaintenance {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("togglemaintenance")
                .requires(commandSource -> commandSource.hasPermission("timenation.togglemaintenance"))
                .executes(this::executeToggleMaintenance)
                .build();

        return new BrigadierCommand(node);
    }

    private int executeToggleMaintenance(CommandContext<CommandSource> context) {
        ICloudProxyGroup iCloudProxyGroup = CloudAPI.getInstance().getCloudServiceGroupManager().getProxyGroupByName("Proxy");

        if (iCloudProxyGroup.isInMaintenance()) {
            iCloudProxyGroup.setMaintenance(false);
            iCloudProxyGroup.update();
            context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.togglemaintenance.false")));
            try {
                TimeVelocityAPI.getInstance().getRabbitMQ().sendMessageToRabbtiMQ("minecraft_on");
            } catch (IOException ignored) { }
            return 1;
        }

        iCloudProxyGroup.setMaintenance(true);
        iCloudProxyGroup.update();
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.togglemaintenance.true")));
        try {
            TimeVelocityAPI.getInstance().getRabbitMQ().sendMessageToRabbtiMQ("minecraft_maintenance");
        } catch (IOException ignored) { }
        return 1;
    }
}
