package net.timenation.velocitysystem.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class JoinMeCommand {

    private static HashMap<Player, Long> joinme = new HashMap<>();

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("joinme").requires(commandSource -> commandSource.hasPermission("timenation.joinme"))
                .executes(JoinMeCommand::sendJoinMe)
                .build();

        return new BrigadierCommand(node);
    }

    private static int sendJoinMe(CommandContext<CommandSource> context) {
        Component component = Components.parse(I18n.format((Player) context.getSource(), "velocity.messages.joinme", (Object) VelocitySystem.getInstance().getProxyPrefix(), TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(((Player) context.getSource()).getUniqueId()).getPlayersRankAndName(((Player) context.getSource()).getUniqueId()), CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUniqueId()).getConnectedServerName())).hoverEvent(new HoverEventSource<Component>() {
            @Override
            public @Nullable HoverEvent<Component> asHoverEvent(@Nullable UnaryOperator<Component> op) {
                return HoverEvent.showText(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.joinme.hovermessage")));
            }
        }).clickEvent(ClickEvent.runCommand("/join " + CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUniqueId()).getConnectedServerName()));

        if (joinme.containsKey((Player) context.getSource())) {
            if (joinme.get((Player) context.getSource()) <= System.currentTimeMillis()) {
                if (!CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUsername()).getConnectedServerName().startsWith("Lobby-") && !CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUsername()).getConnectedServerName().startsWith("SilentLobby-")) {
                    CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUniqueId()).getConnectedServerName()).setProperty("active_joinme", true);
                    CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUniqueId()).getConnectedServerName()).update();
                    VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> {
                        player.sendMessage(component);
                    });
                } else {
                    context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.joinme.notonlobbyserver")));
                }
                if (!context.getSource().hasPermission("timenation.admin")) {
                    joinme.put((Player) context.getSource(), System.currentTimeMillis() + 60000);
                }
            } else {
                context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.joinme.wait")));
            }
        } else {
            if (!CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUsername()).getConnectedServerName().startsWith("Lobby-") && !CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUsername()).getConnectedServerName().startsWith("SilentLobby-")) {
                CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUniqueId()).getConnectedServerName()).setProperty("active_joinme", true);
                CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(((Player) context.getSource()).getUniqueId()).getConnectedServerName()).update();
                VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> {
                    player.sendMessage(component);
                });
            } else {
                context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), VelocitySystem.getInstance().getProxyPrefix(), "velocity.messages.joinme.notonlobbyserver")));
            }
            if (!context.getSource().hasPermission("timenation.admin")) {
                joinme.put((Player) context.getSource(), System.currentTimeMillis() + 60000);
            }
        }

        return 1;
    }
}
