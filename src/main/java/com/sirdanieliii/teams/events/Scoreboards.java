package com.sirdanieliii.teams.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.configuration.ConfigManager.scoreboardUpdate;

public class Scoreboards {
    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    public static org.bukkit.scoreboard.Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();
    public static Map<UUID, Scoreboards> scoreboardTasks = new HashMap<>(); // To store player Scoreboard() objects
    protected int taskID;
    protected UUID uuid;

    public Scoreboards(UUID uuid) {
        this.uuid = uuid;
    }

    public static void reloadScoreboard() {
        scoreboardTasks = new HashMap<>(); // Reset Map / "lists of runnable tasks"
        addAllPlayersToScoreboard();
    }

    public static void addAllPlayersToScoreboard() {
        for (Player p : Bukkit.getOnlinePlayers()) addPlayerToScoreboard(p);
    }

    public static void addPlayerToScoreboard(Player player) {
        Scoreboards pScoreboard = new Scoreboards(player.getUniqueId()); // Create new scoreboards object for player
        scoreboardTasks.put(player.getUniqueId(), pScoreboard); // Map runnable task to player
        pScoreboard.runPlayerScoreboard(player); // Start scoreboard and continuously run it
    }

    public void stopThisScoreboardTask(Map<UUID, Scoreboards> board) {
        Bukkit.getScheduler().cancelTask(taskID);
        board.remove(uuid);
    }

    public void runPlayerScoreboard(Player player) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(getThisPlugin(), () -> {
            // Update scoreboard on player's end
            player.setScoreboard(scoreboard);
        }, 0, scoreboardUpdate);
    }
}
