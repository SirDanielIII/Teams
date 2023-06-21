package com.sirdanieliii.teams.commands;

import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {
    public abstract String getName();

    public abstract String getCmdGroup();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract boolean perform(Player player, String[] args);

    public abstract List<String> getSubcommandArgs(Player player, String[] args);
}
