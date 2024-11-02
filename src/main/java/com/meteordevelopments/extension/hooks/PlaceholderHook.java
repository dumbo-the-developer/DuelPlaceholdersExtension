package com.meteordevelopments.extension.hooks;

import com.meteordevelopments.duels.api.arena.Arena;
import com.meteordevelopments.duels.api.kit.Kit;
import com.meteordevelopments.duels.api.match.Match;
import com.meteordevelopments.duels.api.spectate.Spectator;
import com.meteordevelopments.duels.api.user.User;
import com.meteordevelopments.extension.Placeholder;
import com.meteordevelopments.extension.compat.Ping;
import com.meteordevelopments.extension.util.StringUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderHook extends PlaceholderExpansion {

    Placeholder ext = Placeholder.getPx();
    @Override
    public @NotNull String getIdentifier() {
        return "duels";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Dumbo";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "Player is required";
        }

        User user;

        if (identifier.startsWith("rating_")) {
            user = ext.getUserManager().get(player);

            if (user == null) {
                return StringUtil.color(ext.getUserNotFound());
            }

            identifier = identifier.replace("rating_", "");

            if (identifier.equals("-")) {
                return String.valueOf(user.getRating());
            }

            final Kit kit = ext.getKitManager().get(identifier);
            return kit != null ? String.valueOf(user.getRating(kit)) : StringUtil.color(ext.getNoKit());
        }

        if (identifier.startsWith("match_")) {
            identifier = identifier.replace("match_", "");
            Arena arena = ext.getArenaManager().get(player);

            if (arena == null) {
                final Spectator spectator = ext.getSpectateManager().get(player);

                if (spectator == null) {
                    return StringUtil.color(ext.getNotInMatch());
                }

                arena = spectator.getArena();
                player = spectator.getTarget();

                if (player == null) {
                    return StringUtil.color(ext.getNotInMatch());
                }
            }

            final Match match = arena.getMatch();

            if (match == null) {
                return StringUtil.color(ext.getNotInMatch());
            }

            if (identifier.equalsIgnoreCase("duration")) {
                return DurationFormatUtils.formatDuration(System.currentTimeMillis() - match.getStart(), ext.getDurationFormat());
            }

            if (identifier.equalsIgnoreCase("kit")) {
                return match.getKit() != null ? match.getKit().getName() : StringUtil.color(ext.getNoKit());
            }

            if (identifier.equalsIgnoreCase("arena")) {
                return match.getArena().getName();
            }

            if (identifier.equalsIgnoreCase("bet")) {
                return String.valueOf(match.getBet());
            }

            if (identifier.equalsIgnoreCase("rating")) {
                user = ext.getUserManager().get(player);

                if (user == null) {
                    return StringUtil.color(ext.getUserNotFound());
                }

                return String.valueOf(match.getKit() != null ? user.getRating(match.getKit()) : user.getRating());
            }

            if (identifier.startsWith("opponent")) {
                Player opponent = null;

                for (final Player matchPlayer : match.getPlayers()) {
                    if (!matchPlayer.equals(player)) {
                        opponent = matchPlayer;
                        break;
                    }
                }

                if (opponent == null) {
                    return StringUtil.color(ext.getNoOpponent());
                }

                if (identifier.equalsIgnoreCase("opponent")) {
                    return opponent.getName();
                }

                if (identifier.endsWith("_health")) {
                    return String.valueOf(Math.ceil(opponent.getHealth()) * 0.5);
                }

                if (identifier.endsWith("_ping")) {
                    return String.valueOf(Ping.getPing(opponent));
                }

                user = ext.getUserManager().get(opponent);

                if (user == null) {
                    return StringUtil.color(ext.getUserNotFound());
                }

                return String.valueOf(match.getKit() != null ? user.getRating(match.getKit()) : user.getRating());
            }
        }
        return null;
    }
}
