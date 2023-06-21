package com.sirdanieliii.teams.configuration;

import com.sirdanieliii.teams.configuration.configs.ConfigErrors;
import com.sirdanieliii.teams.configuration.configs.ConfigMain;
import com.sirdanieliii.teams.configuration.configs.ConfigTeams;
import com.sirdanieliii.teams.BasicTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static com.sirdanieliii.teams.BasicTeam.*;
import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.events.Scoreboards.scoreboard;

public class ConfigManager {
    public static String pluginName;
    public static String cmdHeader;
    public static int scoreboardUpdate;
    public static String errorHeader;
    public static String errorClr;
    public static HashMap<String, String> messages = new HashMap<>();
    public static HashMap<Player, BasicTeam> pluginPlayerData = new HashMap<>();
    // HashMap uses more memory, but has O(1) remove time
    // If there's a lot of teams, this is much better than linearly searching through an ArrayList
    public static HashMap<Integer, BasicTeam> pluginTeams = new HashMap<>();
    public static final String blockFooter = "<-------------------------------------------------->";
    public static ConfigTeams configTeams;

    public static void reloadConfigs() {
        ConfigMain config = new ConfigMain("", "config", "", "config");
        configTeams = new ConfigTeams("", "teams", "", "teams");
        ConfigErrors msgs = new ConfigErrors("", "messages", "", "messages");
        config.reload();
        msgs.reload();
        configTeams.reload();
        if (!config.update()) getThisPlugin().getLogger().severe(String.format("Unable to update %s! Problems may occur", config.getFile().getName()));
        if (!msgs.update()) getThisPlugin().getLogger().severe(String.format("Unable to update %s! Problems may occur", msgs.getFile().getName()));
        if (!configTeams.update()) getThisPlugin().getLogger().severe(String.format("Unable to update %s! Problems may occur", configTeams.getFile().getName()));

        pluginName = config.getConfig().getString("name");
        cmdHeader = config.getConfig().getString("cmd_header");
        scoreboardUpdate = config.getConfig().getInt("scoreboard_update");

        errorHeader = msgs.getConfig().getString("error_header");
        errorClr = msgs.getConfig().getString("error_clr");
        for (String key : msgs.getConfig().getKeys(false)) {
            messages.put(key, msgs.getConfig().getString(key));
        }
        // Load teams
        try {
            for (String num : Objects.requireNonNull(configTeams.getConfig().getConfigurationSection("teams")).getKeys(false)) {
                if (validConfig(num)) {
                    int teamNum = parseNumber(num);
                    BasicTeam team = new BasicTeam(
                            scoreboard,
                            teamNum,
                            getChatColor(Objects.requireNonNull(configTeams.getConfig().getString(String.format("teams.%s.colour", num)))));
                    pluginTeams.put(teamNum, team);
                    for (String playerName : Objects.requireNonNull(configTeams.getConfig().getConfigurationSection(String.format("teams.%s.players", num))).getKeys(false)) {
                        Player player = Bukkit.getPlayer(playerName);
                        if (player != null) { // Player is online
                            team.addPlayer(false, player);
                        }
                    }
                } else {
                    configTeams.deleteKey(num);
                    getThisPlugin().getLogger().warning("Deleted " + num + " in teams.yml because it was invalid");
                }
            }
            getThisPlugin().getLogger().info(String.format("Loaded %d teams: %s", numberOfTeams(), pluginTeams.values()));
        } catch (NullPointerException | NumberFormatException ignored) {
        }
    }

    public static String errorMessage(String error) {
        return translateMsgClr(errorHeader + errorClr + " " + messages.get(error));
    }
}