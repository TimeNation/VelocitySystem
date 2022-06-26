package net.timenation.velocitysystem.utils;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamChatList {

    private final List<Player> loggedPlayers;

    public TeamChatList() {
        this.loggedPlayers = new ArrayList<>();
    }
}
