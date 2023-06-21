package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.BasicTeam;
import com.sirdanieliii.teams.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

import static com.sirdanieliii.teams.Utilities.replaceStr;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;

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
        if (!(player.hasPermission("teams.disband"))) {
            player.sendMessage(errorMessage("permission"));
            return false;
        }
        BasicTeam team = pluginPlayerData.get(player);
        if (team == null) {
            player.sendMessage(errorMessage("not_in_team"));
            return false;
        }
        player.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("disbanded_team"),
                "{team}", team.toString()));
        team.disband(player);
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return null;
    }
}
