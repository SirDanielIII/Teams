package com.sirdanieliii.teams.commands.subcommands.teams;

import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Objects;

import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.commands.CommandManager.cmdHeader;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.addAllPlayersToScoreboard;

public class Leave extends SubCommand {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Leaves the team you're currently in";
    }

    @Override
    public String getSyntax() {
        return messages.get("teams_leave");
    }

    @Override
    public boolean perform(Player player, String[] args) {
        Team team = pluginData.get(player);
        if (team == null) {
            player.sendMessage(errorMessage("not_in_team"));
            return false;
        }
        try {
            int teamNumber = Integer.parseInt(Objects.requireNonNull(team).getName()) - 1;
            team.removeEntry(player.getName());
            pluginData.put(player, null);

            List<String> players = teams.getConfig().getStringList(String.format("teams.%s.players", teamNumber + 1));
            players.remove(player.getUniqueId().toString());
            teams.getConfig().set(String.format("teams.%s.players", teamNumber), players);
            teams.save();
            addAllPlayersToScoreboard();

            player.sendMessage(translateMsgClr(cmdHeader("teams") + String.format("You have left Team %s", teamNumber + 1)));
        } catch (NullPointerException | NumberFormatException ignored) {
        }
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return null;
    }
}
