package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.BasicTeam;
import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

import static com.sirdanieliii.teams.Utilities.replaceStr;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;

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
        if (!(player.hasPermission("teams.leave"))) {
            player.sendMessage(errorMessage("permission"));
            return false;
        }
        BasicTeam team = pluginPlayerData.get(player);
        if (team == null) {
            player.sendMessage("not_in_team");
            return false;
        }
        team.removePlayer(player);
        player.sendMessage(translateMsgClr(cmdHeader) + replaceStr(messages.get("left_team"), "{team}", team.toString()));
        team.announceMsg(translateMsgClr(cmdHeader) + replaceStr(messages.get("has_left_team"), "{player}", player.getName()));
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return null;
    }
}
