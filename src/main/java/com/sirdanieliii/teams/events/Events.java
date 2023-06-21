package com.sirdanieliii.teams.events;

import com.sirdanieliii.teams.BasicTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.sirdanieliii.teams.configuration.ConfigManager.pluginPlayerData;
import static com.sirdanieliii.teams.BasicTeam.detectPlayerTeam;
import static com.sirdanieliii.teams.events.Scoreboards.addPlayerToScoreboard;
import static com.sirdanieliii.teams.events.Scoreboards.scoreboardTasks;

public class Events implements Listener {
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BasicTeam team = detectPlayerTeam(player);
        if (team != null) {
            team.addPlayer(false, player);
            pluginPlayerData.put(player, team);
        } else {
            pluginPlayerData.put(player, null);
        }
        addPlayerToScoreboard(player);
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pluginPlayerData.remove(player);
        if (scoreboardTasks.containsKey(player.getUniqueId())) scoreboardTasks.get(player.getUniqueId()).stopThisScoreboardTask(scoreboardTasks);
    }
}
