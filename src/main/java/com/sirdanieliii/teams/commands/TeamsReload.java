package com.sirdanieliii.teams.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.reloadScoreboard;

public class TeamsCmd extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin";
    }

    @Override
    public String getSyntax() {
        return messages.get("teams_reload");
    }

    @Override
    public boolean perform(Player player, String[] args) {
        if (!player.hasPermission("teams.reload")) {
            player.sendMessage(errorMessage("missing_permission"));
            return false;
        }
        reloadConfigs(); // Reload Configs
        reloadScoreboard();
        // Return Messages
        player.sendMessage(translateMsgClr(cmdHeader + " &FAll configs, settings and tasks have been reloaded!"));
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("reload");
        return list;
    }
}
