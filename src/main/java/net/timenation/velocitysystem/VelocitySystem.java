package net.timenation.velocitysystem;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.mysql.MySQL;
import net.timenation.velocitysystem.commands.*;
import net.timenation.velocitysystem.commands.admin.CloudCommand;
import net.timenation.velocitysystem.commands.admin.ShutdownCommand;
import net.timenation.velocitysystem.commands.admin.ToggleMaintenance;
import net.timenation.velocitysystem.commands.team.NotificationCommand;
import net.timenation.velocitysystem.commands.team.TCCommand;
import net.timenation.velocitysystem.commands.team.TeamChatCommand;
import net.timenation.velocitysystem.commands.team.punish.BanCommand;
import net.timenation.velocitysystem.commands.team.punish.MuteCommand;
import net.timenation.velocitysystem.commands.team.punish.UnbanCommand;
import net.timenation.velocitysystem.commands.team.punish.UnmuteCommand;
import net.timenation.velocitysystem.listener.LoginListener;
import net.timenation.velocitysystem.listener.PlayerChatListener;
import net.timenation.velocitysystem.listener.ProxyPingListener;
import net.timenation.velocitysystem.manager.ConfigManager;
import net.timenation.velocitysystem.manager.PunishManager;
import net.timenation.velocitysystem.manager.TablistManager;
import net.timenation.velocitysystem.utils.TeamChatList;

@Plugin(
        id = "velocitysystem",
        name = "VelocitySystem",
        version = BuildConstants.VERSION,
        dependencies = @Dependency(id = "timevelocityapi"),
        authors = {"ByRaudy"}
)
@Getter
public class VelocitySystem {

    private static VelocitySystem instance;
    private final ProxyServer proxyServer;
    private final MySQL mySQL;
    private final ConfigManager configManager;
    private final PunishManager punishManager;
    private final TeamChatList teamChatList;
    private String proxyPrefix;

    @Inject
    public VelocitySystem(ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;
        this.mySQL = TimeVelocityAPI.getInstance().getMySQL();
        this.configManager = new ConfigManager();
        this.punishManager = new PunishManager(TimeVelocityAPI.getInstance().getMySQL());
        this.teamChatList = new TeamChatList();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        EventManager eventManager = proxyServer.getEventManager();
        CommandManager commandManager = proxyServer.getCommandManager();

        this.proxyPrefix = configManager.getString("prefix");

        new TablistManager();

        eventManager.register(this, new LoginListener());
        eventManager.register(this, new ProxyPingListener());
        eventManager.register(this, new PlayerChatListener());

        commandManager.register(new TeamChatCommand().build());
        commandManager.register(new TCCommand().build());
        commandManager.register(new BanCommand().build());
        commandManager.register(new UnbanCommand().build());
        commandManager.register(new MuteCommand().build());
        commandManager.register(new UnmuteCommand().build());
        commandManager.register(new ShutdownCommand().build());
        commandManager.register(new JoinMeCommand().build());
        commandManager.register(new CloudCommand().build());
        commandManager.register(new NotificationCommand().build());
        commandManager.register(new ToggleMaintenance().build());
        commandManager.register(commandManager.metaBuilder("hub").aliases("l", "lobby", "helikopter", "hubschrauber", "kampfhelikopter", "land_der_idioten", "ab_nach_berlin", "ab_in_den_urlaub").build(), new HubCommand());
        commandManager.register(commandManager.metaBuilder("ping").build(), new PingCommand());
        commandManager.register(commandManager.metaBuilder("join").build(), new JoinCommand());
    }

    public static VelocitySystem getInstance() {
        return instance;
    }
}
