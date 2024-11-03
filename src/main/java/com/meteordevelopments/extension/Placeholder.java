package com.meteordevelopments.extension;

import com.meteordevelopments.duels.api.arena.ArenaManager;
import com.meteordevelopments.duels.api.extension.DuelsExtension;
import com.meteordevelopments.duels.api.kit.Kit;
import com.meteordevelopments.duels.api.kit.KitManager;
import com.meteordevelopments.duels.api.queue.DQueueManager;
import com.meteordevelopments.duels.api.spectate.SpectateManager;
import com.meteordevelopments.duels.api.user.UserManager;
import com.meteordevelopments.extension.hooks.PlaceholderHook;
import com.meteordevelopments.extension.util.Updatable;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class Placeholder extends DuelsExtension {
    @Getter
    private String userNotFound;
    @Getter
    private String notInMatch;
    @Getter
    private String durationFormat;
    @Getter
    private String noKit;
    @Getter
    private String noOpponent;

    @Getter
    private static Placeholder px;

    @Getter
    private UserManager userManager;
    @Getter
    private KitManager kitManager;
    @Getter
    private ArenaManager arenaManager;
    @Getter
    private SpectateManager spectateManager;
    @Getter
    private DQueueManager queueManager;

    private final List<Updatable<Kit>> updatables = new ArrayList<>();

    @Override
    public void onEnable() {
        px = this;
        final FileConfiguration config = getConfig();
        this.userNotFound = config.getString("user-not-found");
        this.notInMatch = config.getString("not-in-match");
        this.durationFormat = config.getString("duration-format");
        this.noKit = config.getString("no-kit");
        this.noOpponent = config.getString("no-opponent");

        this.userManager = api.getUserManager();
        this.kitManager = api.getKitManager();
        this.arenaManager = api.getArenaManager();
        this.spectateManager = api.getSpectateManager();
        this.queueManager = api.getQueueManager();

        new PlaceholderHook().register();
    }


    @Override
    public void onDisable() {
        new PlaceholderHook().unregister();
    }

    public void info(final String s) {
        api.info("[" + getName() + " Extension] " + s);
    }

    public void warn(final String s) {
        api.warn("[" + getName() + " Extension] " + s);
    }

    public void error(final String s) {
        api.error("[" + getName() + " Extension] " + s);
    }

    public void error(final String s, final Throwable thrown) {
        api.error("[" + getName() + " Extension] " + s, thrown);
    }
}
