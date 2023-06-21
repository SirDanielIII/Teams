package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.BasicTeam;
import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.sirdanieliii.teams.BasicTeam.getChatColor;
import static com.sirdanieliii.teams.BasicTeam.getNextTeamNumber;
import static com.sirdanieliii.teams.Utilities.replaceStr;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;
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
        if (!(player.hasPermission("teams.create"))) {
            player.sendMessage(errorMessage("permission"));
            return false;
        }
        BasicTeam t = pluginPlayerData.get(player);
        if (t != null) {
            player.sendMessage(errorMessage("currently_in_team"));
            return false;
        }
        if (args.length == 1) { // teams create
            int teamNum = getNextTeamNumber();
            BasicTeam team = new BasicTeam(scoreboard, teamNum);
            team.register();
            team.addPlayer(true, player);
            player.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("created_team"), "{team}", team.toString()));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    p.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("announce_new_team"),
                            "{player}", player.getName(), "{team}", team.toString()));
                }
            }
            return true;
        } else if (args.length == 2) { // teams create [colour]
            ChatColor colour = getChatColor(args[1]);
            if (colour == null) {
                player.sendMessage(errorMessage("not_valid_colour"));
                return false;
            }
            int teamNum = getNextTeamNumber();
            BasicTeam team = new BasicTeam(scoreboard, teamNum, colour);
            team.register();
            team.addPlayer(true, player);
            player.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("created_team"), "{team}", team.toString()));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    p.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("announce_new_team"),
                            "{player}", player.getName(), "{team}", team.toString()));
                }
            }
            return true;
        } else if (args.length > 2) {
            player.sendMessage(errorMessage("too_many_arguments"));
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
