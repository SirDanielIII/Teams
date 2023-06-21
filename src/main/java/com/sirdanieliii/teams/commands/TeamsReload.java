package com.sirdanieliii.teams.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.Utilities.translateMsgClrComponent;
import static com.sirdanieliii.teams.commands.CommandManager.cmdCategories;
import static com.sirdanieliii.teams.commands.CommandManager.cmdClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.reloadScoreboard;

public class TeamsReload implements TabExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player player) {
                    if (!(player.hasPermission("teams.admin"))) {
                        player.sendMessage(errorMessage("permission"));
                        return false;
                    }
                }
                reloadConfigs();
                reloadScoreboard();
                // Return Messages
                sender.sendMessage(translateMsgClr(cmdHeader + " &FAll configs, settings and tasks have been reloaded!"));
                if (sender instanceof Player) getThisPlugin().getLogger().info(sender.getName() + "has reloaded the " + pluginName + " plugin!");
                return true;
            }
            sender.sendMessage(errorMessage("smp"));
            return false;
        }
        // Display all plugin commands
        sender.sendMessage(translateMsgClr("------------ | " + pluginName + " Commands &R&F| ------------>"));
        TextComponent cmdReload = translateMsgClrComponent("→ " + cmdClr(cmd.getName(), false) + messages.get("teams_reload"));
        cmdReload.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, messages.get("teams_reload")));
        cmdReload.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateMsgClr("&7Reloads all configs, settings and tasks for this plugin"))));
        sender.spigot().sendMessage(cmdReload);
        for (List<SubCommand> subcommands : cmdCategories.values()) {
            for (SubCommand subcommand : subcommands) {
                TextComponent command = translateMsgClrComponent("→ " + cmdClr(subcommand.getCmdGroup(), false) + subcommand.getSyntax());
                command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subcommand.getSyntax()));
                command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateMsgClr("&7" + subcommand.getDescription()))));
                sender.spigot().sendMessage(command);
            }
        }
        sender.sendMessage(blockFooter);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("reload");
        return list;
    }
}