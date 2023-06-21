package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.BasicTeam;
import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.sirdanieliii.teams.BasicTeam.getAllTeamsOrdered;
import static com.sirdanieliii.teams.BasicTeam.parseNumber;
import static com.sirdanieliii.teams.Utilities.replaceStr;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.scoreboard;

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
        if (!(player.hasPermission("teams.join"))) {
            player.sendMessage(errorMessage("permission"));
            return false;
        }
        BasicTeam t = pluginPlayerData.get(player);
        if (t == null) {
            player.sendMessage("not_in_team");
            return false;
        }
        if (args.length == 2) { // teams join [number]
            int teamNumber = parseNumber(args[1]);
            if (teamNumber == 0) {
                player.sendMessage(errorMessage("invalid_team_number"));
                return false;
            }
            BasicTeam team = new BasicTeam(scoreboard, teamNumber);
            team.announceMsg(replaceStr(messages.get("has_joined_team"), "{player}", player.getName()));
            team.addPlayer(true, player);
            player.sendMessage(translateMsgClr(cmdHeader + " " + messages.get("joined_team")));
        } else if (args.length > 2) {
            player.sendMessage(errorMessage("too_many_arguments"));
        }
        return false;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> lst = new ArrayList<>();
        if (args.length == 2) {
            for (int i : getAllTeamsOrdered()) {
                lst.add(String.valueOf(i));
            }
        }
        return lst;
    }
}
