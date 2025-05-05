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
        switch (identifier) {
            case "wins":
                user = ext.getUserManager().get(player);

                if (user == null) {
                    return StringUtil.color(ext.getUserNotFound());
                }
                return String.valueOf(user.getWins());
            case "losses":
                user = ext.getUserManager().get(player);
                if (user == null) {
                    return StringUtil.color(ext.getUserNotFound());
                }
                return String.valueOf(user.getLosses());
            case "can_request":
                user = ext.getUserManager().get(player);
                if (user == null) {
                    return StringUtil.color(ext.getUserNotFound());
                }
                return String.valueOf(user.canRequest());
            //case "hits": {
            //    Arena arena = ext.getArenaManager().get(player);
            //    // Only activate when winner is undeclared
            //    if (arena == null) {
            //        return "-1";
            //    }
            //    return String.valueOf(arena.getMatch().getHits(player));
            //}
            //case "hits_opponent": {
            //    Arena arena = ext.getArenaManager().get(player);
            //    // Only activate when winner is undeclared
            //    if (arena == null) {
            //        return "-1";
            //    }
            //    return String.valueOf(arena.getMatch().getHits(arena.getOpponent(player)));
            //}
            case "wl_ratio":
            case "wlr":
                user = ext.getUserManager().get(player);
                if (user == null) {
                    return StringUtil.color(ext.getUserNotFound());
                }
                int wins = user.getWins();
                int losses = user.getLosses();
                return String.valueOf(wlr(wins, losses));
        }

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

        if (identifier.startsWith("getplayersinqueue_")){
            user = ext.getUserManager().get(player);
            if (user == null) {
                return StringUtil.color(ext.getUserNotFound());
            }

            identifier = identifier.replace("getplayersinqueue_", "");

            final Kit kit = ext.getKitManager().get(identifier);
            if (kit == null) {
                return StringUtil.color(ext.getNoKit());
            }

            int queuedPlayers = ext.getQueueManager().get(kit, 0).getQueuedPlayers().size();
            return queuedPlayers > 0 ? String.valueOf(queuedPlayers) : "0";
        }

        if (identifier.startsWith("getplayersplayinginqueue_")){
            user = ext.getUserManager().get(player);
            if (user == null) {
                return StringUtil.color(ext.getUserNotFound());
            }
            identifier = identifier.replace("getplayersplayinginqueue_", "");
            final Kit kit = ext.getKitManager().get(identifier);
            if (kit == null) {
                return StringUtil.color(ext.getNoKit());
            }
            long playersInMatch = ext.getQueueManager().get(kit, 0).getPlayersInMatch();
            return Long.toString(playersInMatch);
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

    private float wlr(int wins, int losses) {
        if (wins == 0) {
            return losses == 0 ? 0.0F : (float)(-losses);
        } else if (losses == 0) {
            return (float)wins;
        } else {
            return (float)(wins / losses);
        }
    }
}
