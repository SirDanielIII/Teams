package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.commands.SubCommand;
import com.sirdanieliii.teams.BasicTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.sirdanieliii.teams.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.BasicTeam.getAllTeamsOrdered;

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
            player.sendMessage("not_in_team");
            return false;
        }
        team.disband(player);
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        return null;
    }
}
