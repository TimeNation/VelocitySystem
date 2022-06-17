package net.timenation.velocitysystem.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

public class PlayerChatListener {

    @Subscribe
    public void handlePlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if(VelocitySystem.getInstance().getPunishManager().ismuted(player.getUniqueId())) {
            long current = System.currentTimeMillis();
            long end = VelocitySystem.getInstance().getPunishManager().getMuteEnd(player.getUniqueId());
            if(((current < end ? 1 : 0) | (end == -1L ? 1 : 0)) != 0) {
                if(!event.getMessage().startsWith("/")) {
                    player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.messages.punish.ismuted", VelocitySystem.getInstance().getPunishManager().getMuteReason(player.getUniqueId()), VelocitySystem.getInstance().getPunishManager().getMuteReamainingTime(player, player.getUniqueId()))));
                    event.setResult(PlayerChatEvent.ChatResult.denied());
                }
            } else {
                VelocitySystem.getInstance().getPunishManager().unmutePlayer(player.getUniqueId());
            }
        }
    }
}
