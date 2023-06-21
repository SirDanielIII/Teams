package com.sirdanieliii.SD_SMP.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.sirdanieliii.SD_SMP.SD_SMP.getPlugin;
import static com.sirdanieliii.SD_SMP.configuration.ConfigManager.healthUnderNameText;
import static com.sirdanieliii.SD_SMP.configuration.ConfigManager.healthUnderNameUpdate;
import static com.sirdanieliii.SD_SMP.utilities.Utilities.translateMsgClr;

public class Scoreboards {
    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    public static org.bukkit.scoreboard.Scoreboard scoreboard = Objects.requireNonNull(manager).getNewScoreboard();
    public static Map<UUID, Scoreboards> scoreboardTasks = new HashMap<>(); // To store player Scoreboard() objects
    protected int taskID;
    protected UUID uuid;

    public Scoreboards(UUID uuid) {
        this.uuid = uuid;
    }

    public static void registerHealthScoreboard() {
        Objective health = scoreboard.registerNewObjective("player_health", "dummy", ""); // Name, Criteria / Objective, Display Name
        health.setDisplaySlot(DisplaySlot.BELOW_NAME); // Always will have a number score (int) before it
        health.setDisplayName(translateMsgClr(healthUnderNameText));
    }

    public static void reloadHealthScoreboard() {
        disableHealthScoreboard();
        registerHealthScoreboard();
        scoreboardTasks = new HashMap<>(); // Reset Map / "lists of runnable tasks"
        addAllPlayersToScoreboard();
    }

    public static void disableHealthScoreboard() {
        // Deregister scoreboard objective
        if (scoreboard.getObjective("player_health") != null) Objects.requireNonNull(scoreboard.getObjective("player_health")).unregister();
        // Stop all player Scoreboard() tasks
        for (UUID uuid : scoreboardTasks.keySet()) scoreboardTasks.get(uuid).stopThisScoreboardTask(scoreboardTasks);
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
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), () -> {
            // Update health scoreboard below player's name
            Objective obj = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
            // Set score to player's health
            Objects.requireNonNull(obj).getScore(player.getName()).setScore((int) Math.ceil(player.getHealth()));
            // Update scoreboard on player's end
            player.setScoreboard(scoreboard);
        }, 0, healthUnderNameUpdate);
    }
}
