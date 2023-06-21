package com.sirdanieliii.teams.commands.subcommands.teams;

import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.addAllPlayersToScoreboard;

public class Disband extends SubCommand {
    @Override
    public String getName() {
        return "disband";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Deletes the team you're currently in";
    }

    @Override
    public String getSyntax() {
        return messages.get("teams_disband");
    }

    @Override
    public boolean perform(Player player, String[] args) {
        Team team = pluginData.get(player);
        if (team == null) {
            player.sendMessage(errorMessage("not_in_team"));
            return false;
        }
        try {
            teams.deleteKey(String.format("teams.%s", Integer.parseInt(Objects.requireNonNull(team).getName()) + 1));
        } catch (NumberFormatException e) {
            getThisPlugin().getLogger().warning("Could not delete Team " + team.getName() + " from teams.yml");
        }
        team.unregister();
        addAllPlayersToScoreboard();
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> lst = new ArrayList<>();
        for (int i = 1; i <= pluginTeams.size(); i++) {
            lst.add(String.valueOf(i));
        }
        return lst;
    }
}
/**
 * REWORK PLUGIN STRUCTURE - WHENEVER YOU DISBAND THE ORDER OF THE TEAMS GETS MESSED UP - NEED TO DETECT NEXT ONE TO FILL IN
 * COLOUR OF NAME TAG DOES NOT WORKING PROPERLY
 */