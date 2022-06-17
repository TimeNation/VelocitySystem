package net.timenation.velocitysystem.commands.team.punish;

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
import net.kyori.adventure.text.Component;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UnbanCommand {

    public BrigadierCommand build() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("unban").requires(commandSource -> commandSource.hasPermission("timenation.team.unpunish"))
                .executes(this::sendHelp)
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("playername", StringArgumentType.string())
                        .suggests(this::setBannedPlayersSuggestions)
                        .executes(context -> unban((Player) context.getSource(), TimeVelocityAPI.getInstance().getUuidFetcher().getUUID(context.getArgument("playername", String.class)))))
                .build();

        return new BrigadierCommand(node);
    }

    private int sendHelp(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Components.parse(I18n.format((Player) context.getSource(), I18n.format((Player) context.getSource(), "velocity.prefix.punish"), "velocity.messages.unpunish.info")));
        return 1;
    }

    private static String getPunishPrefix(Player player) {
        return I18n.format(player, "velocity.prefix.punish");
    }

    private CompletableFuture<Suggestions> setBannedPlayersSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder suggestionsBuilder) {
        VelocitySystem.getInstance().getPunishManager().getBannedPlayers().forEach(suggestionsBuilder::suggest);
        return suggestionsBuilder.buildFuture();
    }

    private int unban(Player player, UUID target) {
        if(VelocitySystem.getInstance().getPunishManager().isBanned(target)) {
            VelocitySystem.getInstance().getPunishManager().unbanPlayer(target, player);
            return 1;
        }

        player.sendMessage(Components.parse(I18n.format(player, getPunishPrefix(player), "velocity.messages.playerisntbanned")));
        return 1;
    }
}
