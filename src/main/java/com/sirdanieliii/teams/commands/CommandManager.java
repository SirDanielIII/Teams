package com.sirdanieliii.teams.commands;

import com.sirdanieliii.teams.commands.subcommands.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.Utilities.translateMsgClrComponent;
import static com.sirdanieliii.teams.configuration.ConfigManager.blockFooter;

public class CommandManager implements TabExecutor {
    public static Map<String, List<SubCommand>> cmdCategories = new HashMap<>();
    public static List<SubCommand> teams = new ArrayList<>();

    public CommandManager() {
        teams.add(new Create());
        teams.add(new Disband());
        teams.add(new Invite());
        teams.add(new Join());
        teams.add(new Kick());
        teams.add(new Leave());
        cmdCategories.put("teams", teams);
    }

    public static ArrayList<SubCommand> getSubcommands(String key) {
        return (ArrayList<SubCommand>) cmdCategories.get(key);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length > 0) { // Provided arguments
            for (SubCommand subcommand : getSubcommands(cmd.getName())) { // Loop through all subcommands of category
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    subcommand.perform(player, args);  // Check for any command family matches and perform it
                    return true;
                }
            }
        }
        displaySubCommands(player, cmd.getName());  // If no subcommand matches, display all possible subcommands
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        if (args.length == 1) { // E.G. /command [TAB-COMPLETE]
            ArrayList<String> subcommandArgs = new ArrayList<>();
            for (int i = 0; i < getSubcommands(cmd.getName()).size(); i++)
                subcommandArgs.add(getSubcommands(cmd.getName()).get(i).getName());  // Get subcommand names as tab-complete
            return subcommandArgs;
        } else if (args.length >= 2) { // /command subcommand [TAB-COMPLETE]
            for (int i = 0; i < getSubcommands(cmd.getName()).size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands(cmd.getName()).get(i).getName())) {
                    return getSubcommands(cmd.getName()).get(i).getSubcommandArgs((Player) sender, args);  // Get matching subcommand's tab-complete
                }
            }
        }
        return null;
    }

    private void displaySubCommands(Player player, String cmd) {
        String name = cmdClr(cmd, true) + cmd.toUpperCase();
        player.sendMessage(translateMsgClr("------------ | " + name + " &R&F| ------------>"));
        for (SubCommand subcommand : cmdCategories.get(cmd)) {
            TextComponent command = translateMsgClrComponent("→ " + cmdClr(subcommand.getCmdGroup(), false) + subcommand.getSyntax());
            command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subcommand.getSyntax()));
            command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateMsgClr("&7" + subcommand.getDescription()))));
            player.spigot().sendMessage(command);
        }
        player.sendMessage(blockFooter);
    }

    public static String cmdClr(String type, boolean bold) {
        String s;
        switch (type.toLowerCase()) {
            case "teams" -> s = "&B";
            default -> s = "&F";
        }
        if (bold) s += "&L";
        return s;
    }
}