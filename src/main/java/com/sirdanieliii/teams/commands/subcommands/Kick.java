package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.BasicTeam;
import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.sirdanieliii.teams.Utilities.replaceStr;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;

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
        if (!(player.hasPermission("teams.kick"))) {
            player.sendMessage(errorMessage("permission"));
            return false;
        }
        BasicTeam team = pluginPlayerData.get(player);
        if (team == null) {
            player.sendMessage("not_in_team");
            return false;
        }
        if (args.length == 1) {
            player.sendMessage(errorMessage("missing_players"));
            return false;
        } else { // teams kick [...players]
            for (String i : Arrays.copyOfRange(args, 1, args.length)) {
                Player recipient = Bukkit.getPlayer(i);
                if (recipient == null) continue;
                team.removePlayer(recipient);
                player.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("kicked_player"),
                        "{player}", recipient.getName(), "{team}", team.toString()));
                team.announceMsg(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("has_kicked_someone_out_of_team"),
                        "{player_1}", player.getName(), "{player_2}", recipient.getName()), player);
                recipient.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("kicked_from_team"),
                        "{player}", player.getName(), "{team}", team.toString()));
            }
            return true;
        }
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> lst = new ArrayList<>();
        if (args.length >= 2) {
            Collection<? extends Player> playerLst = Bukkit.getOnlinePlayers();
            for (String i : args) {
                for (Player p : playerLst) {
                    if (p.getName().startsWith(i)) lst.add(p.getName());
                }
            }
        }
        return lst;
    }
}
