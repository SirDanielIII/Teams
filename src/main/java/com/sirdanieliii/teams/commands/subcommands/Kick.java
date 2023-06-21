package com.sirdanieliii.teams.commands.subcommands.teams;

import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.commands.CommandManager.cmdHeader;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.addAllPlayersToScoreboard;

public class Kick extends SubCommand {
    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Kicks a player out of the team";
    }

    @Override
    public String getSyntax() {
        return "/teams kick";
    }

    @Override
    public boolean perform(Player player, String[] args) {
        Team team = pluginData.get(player);
        if (team == null) {
            player.sendMessage(errorMessage("not_in_team"));
            return false;
        }
        for (String i : Arrays.copyOfRange(args, 1, args.length)) {
            Player recipient = Bukkit.getPlayer(i);
            if (recipient == null) continue;
            try {
                int teamNumber = Integer.parseInt(Objects.requireNonNull(team).getName()) - 1;
                team.removeEntry(recipient.getName());
                pluginData.put(recipient, null);

                List<String> players = teams.getConfig().getStringList(String.format("teams.%s.players", teamNumber + 1));
                players.remove(player.getUniqueId().toString());
                teams.getConfig().set(String.format("teams.%s.players", teamNumber), players);
                teams.save();
                addAllPlayersToScoreboard();

                player.sendMessage(translateMsgClr(cmdHeader("teams") + String.format("You have kicked %s out of the team!", recipient.getName())));
            } catch (NullPointerException | NumberFormatException ignored) {
            }
        }
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> list = new ArrayList<>();
        Collection<? extends Player> playerLst = Bukkit.getOnlinePlayers();
        if (args.length >= 2) {
            for (String i : args) {
                for (Player p : playerLst) {
                    if (p.getName().startsWith(i)) list.add(p.getName());
                }
            }
        }
        return list;
    }
}
