package net.timenation.velocitysystem.commands.admin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.network.component.ManagerComponent;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import eu.thesimplecloud.api.service.ICloudService;
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup;
import eu.thesimplecloud.module.permission.PermissionPool;
import eu.thesimplecloud.module.permission.player.PlayerPermissionGroupInfo;
import net.kyori.adventure.text.Component;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.velocitysystem.VelocitySystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class CloudCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("cloud")
                .requires(commandSource -> commandSource.hasPermission("timenation.admin.timecloud"))
                .then(LiteralArgumentBuilder.<CommandSource>literal("create")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("by")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("cloudGroup", StringArgumentType.string())
                                        .suggests(this::setCloudGroupsSuggestions)
                                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("count", StringArgumentType.string())
                                                .then(LiteralArgumentBuilder.<CommandSource>literal("--start")
                                                        .executes(context -> executeStartService(context, CloudAPI.getInstance().getCloudServiceGroupManager().getServiceGroupByName(context.getArgument("cloudGroup", String.class)), Integer.parseInt(context.getArgument("count", String.class)))))))))
                .then(LiteralArgumentBuilder.<CommandSource>literal("players")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("player")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.string())
                                        .suggests(this::setOnlinePlayerSuggestions)
                                        .then(LiteralArgumentBuilder.<CommandSource>literal("info")
                                                .executes(context -> sendCloudPlayerInfo(context, CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(context.getArgument("player", String.class)).getBlockingOrNull())))
                                        .then(LiteralArgumentBuilder.<CommandSource>literal("setgroup")
                                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("rank", StringArgumentType.string())
                                                        .suggests(this::setRanksSuggestions)
                                                        .executes(context -> setCloudPlayersRank(context, context.getArgument("player", String.class), context.getArgument("rank", String.class)))))))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("list")
                                .then(LiteralArgumentBuilder.<CommandSource>literal("online")
                                        .executes(this::sendOnlineCloudPlayers))))
                .then(LiteralArgumentBuilder.<CommandSource>literal("service")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("listall")
                                .executes(this::listOnlineCloudServices))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("get")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("service", StringArgumentType.string())
                                        .suggests(this::setCloudServicesSuggestions)
                                        .then(LiteralArgumentBuilder.<CommandSource>literal("stop")
                                                .executes(context -> shutdownCloudService(context, CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(context.getArgument("service", String.class)))))
                                        .then(LiteralArgumentBuilder.<CommandSource>literal("info")
                                                .executes(context -> listInfoCloudService(context, CloudAPI.getInstance().getCloudServiceManager().getCloudServiceByName(context.getArgument("service", String.class))))))))
                .then(LiteralArgumentBuilder.<CommandSource>literal("delete")
                        .then(LiteralArgumentBuilder.<CommandSource>literal("by")
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("serviceGroup", StringArgumentType.string())
                                        .suggests(this::setCloudGroupsSuggestions)
                                        .then(LiteralArgumentBuilder.<CommandSource>literal("--stop")
                                                .executes(context -> shutdownCloudGroupService(context, CloudAPI.getInstance().getCloudServiceGroupManager().getServiceGroupByName(context.getArgument("serviceGroup", String.class))))))))
                .build();

        return new BrigadierCommand(node);
    }

    private CompletableFuture<Suggestions> setCloudGroupsSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        CloudAPI.getInstance().getCloudServiceGroupManager().getAllCachedObjects().forEach(iCloudServiceGroup -> suggestionsBuilder.suggest(iCloudServiceGroup.getName()));
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> setOnlinePlayerSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> suggestionsBuilder.suggest(player.getUsername()));
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> setRanksSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        PermissionPool.getInstance().getPermissionGroupManager().getAllPermissionGroups().forEach(iPermissionGroup -> suggestionsBuilder.suggest(iPermissionGroup.getName()));
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> setCloudServicesSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        CloudAPI.getInstance().getCloudServiceManager().getAllCachedObjects().forEach(iCloudService -> suggestionsBuilder.suggest(iCloudService.getName()));
        return suggestionsBuilder.buildFuture();
    }

    private int sendCloudPlayerInfo(CommandContext<CommandSource> context, IOfflineCloudPlayer iCloudPlayer) {
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd:MM:yy");
        PlayerPermissionGroupInfo playerPermissionGroupInfo = PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(iCloudPlayer.getName()).getBlockingOrNull().getPermissionGroupInfoList().stream().toList().get(0);
        System.out.println(PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(iCloudPlayer.getName()).getBlockingOrNull().getPermissionGroupInfoList().stream().toList().size());
        if (PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(iCloudPlayer.getName()).getBlockingOrNull().getPermissionGroupInfoList().stream().toList().size() >= 2 && PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(iCloudPlayer.getName()).getBlockingOrNull().getPermissionGroupInfoList().stream().toList().get(0).getPermissionGroupName().equals("default")) {
            playerPermissionGroupInfo = PermissionPool.getInstance().getPermissionPlayerManager().getPermissionPlayer(iCloudPlayer.getName()).getBlockingOrNull().getPermissionGroupInfoList().stream().toList().get(1);
        }

        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7" + iCloudPlayer.getName() + " §8/ §7" + iCloudPlayer.getUniqueId()));
        if (playerPermissionGroupInfo.getTimeoutTimestamp() == -1) {
            context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7" + playerPermissionGroupInfo.getPermissionGroupName() + " §8/ §4§lPermanent"));
        } else {
            context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7" + playerPermissionGroupInfo.getPermissionGroupName() + " §8/ §e" + simpleDateFormatDate.format(new Date(playerPermissionGroupInfo.getTimeoutTimestamp())).replace(":", ".")));
        }
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7First login§8: §7" + simpleDateFormatDate.format(new Date(iCloudPlayer.getFirstLogin())).replace(":", ".") + " §8/ §7Last login§8: §7" + simpleDateFormatDate.format(new Date(iCloudPlayer.getLastLogin())).replace(":", ".")));
        return 1;
    }

    private int sendOnlineCloudPlayers(CommandContext<CommandSource> context) {
        CloudAPI.getInstance().getCloudPlayerManager().getAllCachedObjects().forEach(iCloudPlayer -> context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7" + iCloudPlayer.getName() + " §8/ §7" + iCloudPlayer.getUniqueId() + " §8/ §7" + iCloudPlayer.getConnectedServerName())));
        return 1;
    }

    private int setCloudPlayersRank(CommandContext<CommandSource> context, String playerName, String rank) {
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7The rank of §e" + playerName + " §7was set to §6" + rank + "§8."));
        ManagerComponent.INSTANCE.executeCommand("perms user " + playerName + " group set " + rank);

        if(VelocitySystem.getInstance().getProxyServer().getPlayer(playerName).isPresent()) {
            VelocitySystem.getInstance().getProxyServer().getPlayer(playerName).get().disconnect(Component.text(I18n.format(VelocitySystem.getInstance().getProxyServer().getPlayer(playerName).get(), "velocity.kickscreen.newrank", (Object) rank)));
        }
        return 1;
    }

    private int executeStartService(CommandContext<CommandSource> context, ICloudServiceGroup iCloudServiceGroup, int count) {
        for (int i = 0; i < count; i++) {
            iCloudServiceGroup.startNewService();
        }
        context.getSource().sendMessage(Component.text(I18n.format((Player) context.getSource(), CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix"), "velocity.messages.cloud.startservices")));
        return 1;
    }

    private int shutdownCloudService(CommandContext<CommandSource> context, ICloudService iCloudService) {
        iCloudService.shutdown();
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§e" + iCloudService.getName() + " §7will be stopped§8."));
        return 1;
    }

    private int shutdownCloudGroupService(CommandContext<CommandSource> context, ICloudServiceGroup iCloudServiceGroup) {
        iCloudServiceGroup.getAllServices().forEach(iCloudService -> iCloudService.shutdown());
        return 1;
    }

    private int listInfoCloudService(CommandContext<CommandSource> context, ICloudService iCloudService) {
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + ""));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7Info of §e" + iCloudService.getName() + "§8(§e" + iCloudService.getServiceGroup().getName() + "§8)"));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + ""));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7IP§8: §e" + iCloudService.getHost() + " §8/ §7Port§8: §e" + iCloudService.getPort()));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7Online players§8: §e" + iCloudService.getOnlineCount() + " §8/ §7Max players§8: §e" + iCloudService.getMaxPlayers()));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7Used Memory§8: §e" + iCloudService.getUsedMemory() + " §8/ §7Max Memory§8: §e" + iCloudService.getMaxMemory()));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7MOTD§8: §e" + iCloudService.getMOTD()));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7Node§8: §e" + iCloudService.getWrapper().getName() + " §8(§7IP§8: §e" + iCloudService.getWrapper().getHost() + " §8| §7Used Memory§8: §e" + iCloudService.getWrapper().getUsedMemory() + " §8| §7Max Memory§8: §e" + iCloudService.getWrapper().getMaxMemory() + "§8)"));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7ServiceType§8: §e" + iCloudService.getServiceType() + " §8/ §7State§8: §e" + iCloudService.getState()));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + ""));
        return 1;
    }

    private int listOnlineCloudServices(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + ""));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7List of all online ICloudServices"));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + ""));
        CloudAPI.getInstance().getCloudServiceManager().getAllCachedObjects().forEach(iCloudService -> context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + "§7" + iCloudService.getName() + "§8(§7" + iCloudService.getGroupName() + "§8) §8/ §7ServiceState§8: §e" + iCloudService.getState() + " §8/ §7Online players§8: §e" + iCloudService.getOnlineCount() + " §8| §7Max players§8: §e" + iCloudService.getMaxPlayers())));
        context.getSource().sendMessage(Component.text(CloudAPI.getInstance().getLanguageManager().getMessage("cloud.prefix") + ""));
        return 1;
    }
}
