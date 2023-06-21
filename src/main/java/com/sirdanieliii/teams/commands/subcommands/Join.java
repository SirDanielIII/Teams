package com.sirdanieliii.teams.commands.subcommands.teams;

import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.commands.CommandManager.cmdHeader;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.addAllPlayersToScoreboard;

public class Join extends SubCommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Lets you join a team";
    }

    @Override
    public String getSyntax() {
        return messages.get("teams_join");
    }

    @Override
    public boolean perform(Player player, String[] args) {
        if (args.length == 2) {
            try {
                int teamNumber = Integer.parseInt(args[1]) - 1;
                if (teamNumber >= numberOfTeams() - 1) {
                    player.sendMessage(translateMsgClr(errorMessage("not_valid_team")));
                    return false;
                }
                Team team = pluginTeams.get(teamNumber);
                team.addEntry(player.getName());
                pluginData.put(player, team);
                List<String> players = teams.getConfig().getStringList(String.format("teams.%s.players", teamNumber));
                players.add(player.getUniqueId().toString());
                teams.getConfig().set(String.format("teams.%s.players", teamNumber), players);
                teams.save();
                addAllPlayersToScoreboard();
                player.sendMessage(translateMsgClr(cmdHeader("teams") + "you have joined Team" + team.getName()));
            } catch (NumberFormatException e) {
                player.sendMessage(errorMessage("not_valid_team"));
            }
        }
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> teams = new ArrayList<>();
        if (args.length == 2) {
            for (int i = 1; i <= numberOfTeams(); i++) {
                teams.add(String.valueOf(i));
            }
        }
        return teams;
    }
}
