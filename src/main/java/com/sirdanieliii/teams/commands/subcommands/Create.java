package com.sirdanieliii.teams.commands.subcommands.teams;

import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.commands.CommandManager.cmdHeader;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.addAllPlayersToScoreboard;
import static com.sirdanieliii.teams.events.Scoreboards.scoreboard;

public class Create extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Creates a team";
    }

    @Override
    public String getSyntax() {
        return messages.get("teams_create");
    }

    @Override
    public boolean perform(Player player, String[] args) {
        if (args.length == 1) { // teams create
            ChatColor colour = getRandomColor();
            if (colour == null) {
                player.sendMessage(errorMessage("not_valid_colour"));
                return false;
            }
            int teamNumber = numberOfTeams() + 1;
            scoreboard.registerNewTeam(String.valueOf(teamNumber));
            teams.getConfig().set(String.format("teams.%s.colour", teamNumber), colour.name());
            teams.getConfig().set(String.format("teams.%s.players", teamNumber), Collections.singletonList(player.getName()));
            teams.save();
            addAllPlayersToScoreboard();
            player.sendMessage(translateMsgClr(cmdHeader("teams") + "Created & joined Team " + teamNumber));
            return true;
        } else if (args.length == 2) {
            ChatColor colour = getChatColor(args[1]);
            if (colour == null) {
                player.sendMessage(errorMessage("not_valid_colour"));
                return false;
            }
            int teamNumber = numberOfTeams() + 1;
            scoreboard.registerNewTeam(String.valueOf(teamNumber));
            teams.getConfig().set(String.format("teams.%s.colour", teamNumber), args[1].toUpperCase());
            teams.getConfig().set(String.format("teams.%s.players", teamNumber), Collections.singletonList(player.getName()));
            teams.save();
            addAllPlayersToScoreboard();
            player.sendMessage(translateMsgClr(cmdHeader("teams") + "Created & joined Team " + teamNumber));
            return true;
        }
        return false;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> lst = new ArrayList<>();
        if (args.length == 2) {
            for (String msg : List.of("BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY",
                    "DARK_GRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHT_PURPLE", "YELLOW", "WHITE")) {
                if (msg.startsWith(args[1])) {
                    lst.add(msg);
                }
            }
        }
        return lst;
    }
}
