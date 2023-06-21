package com.sirdanieliii.teams;

import com.sirdanieliii.teams.commands.CommandManager;
import com.sirdanieliii.teams.commands.TeamsReload;
import com.sirdanieliii.teams.events.Events;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.sirdanieliii.teams.configuration.ConfigManager.reloadConfigs;
import static com.sirdanieliii.teams.events.Scoreboards.addAllPlayersToScoreboard;

public final class Teams extends JavaPlugin {
    private static Teams instance;

    public Teams() {
        instance = this;
    }

    public static Teams getThisPlugin() {
        return instance;
    }

    public static Server getThisPluginServer() {
        return getThisPlugin().getServer();
    }

    @Override
    public void onEnable() {
        reloadConfigs();
        getServer().getPluginManager().registerEvents(new Events(), this);
        Objects.requireNonNull(getCommand("teams")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("teamsreload")).setExecutor(new TeamsReload());
        addAllPlayersToScoreboard(); // In case of server forced plugin reloads
        this.getLogger().info(String.format("Version %s has finished loading", this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Shutting down plugin...");
        this.getServer().getScheduler().cancelTasks(this);
    }
}
