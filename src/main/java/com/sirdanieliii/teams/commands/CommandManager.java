package com.sirdanieliii.teams.commands;

import com.sirdanieliii.SD_SMP.commands.subcommands.coords.coordsClear;
import com.sirdanieliii.SD_SMP.commands.subcommands.coords.coordsList;
import com.sirdanieliii.SD_SMP.commands.subcommands.coords.coordsSend;
import com.sirdanieliii.SD_SMP.commands.subcommands.coords.coordsSet;
import com.sirdanieliii.SD_SMP.commands.subcommands.death.*;
import com.sirdanieliii.SD_SMP.commands.subcommands.ivan.*;
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

import static com.sirdanieliii.SD_SMP.configuration.ConfigManager.blockFooter;
import static com.sirdanieliii.SD_SMP.utilities.Utilities.translateMsgClr;
import static com.sirdanieliii.SD_SMP.utilities.Utilities.translateMsgClrComponent;

public class CommandManager implements TabExecutor {
    public static Map<String, List<SubCommand>> cmdCategories = new HashMap<>();
    List<SubCommand> ivan = new ArrayList<>();
    List<SubCommand> coords = new ArrayList<>();
    List<SubCommand> death = new ArrayList<>();

    public CommandManager() {
        ivan.add(new ivanDog());
        ivan.add(new ivanDonkey());
        coords.add(new coordsClear());
        coords.add(new coordsList());
        coords.add(new coordsSend());
        coords.add(new coordsSet());
        death.add(new deathKDR());
        death.add(new deathKills());
        death.add(new deathNonPlayer());
        death.add(new deathPlayer());
        death.add(new deathTotal());
        cmdCategories.put("ivan", ivan);
        cmdCategories.put("death", death);
        cmdCategories.put("coords", coords);
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

    public static String cmdHeader(String type) {
        return switch (type.toLowerCase()) {
            case ("coords") -> "&6[&FCoords&6] ";
            case ("death") -> "&4[&FDeath&4] ";
            case ("ivan") -> "&B[&FIvan&B] ";
            default -> "";
        };
    }

    public static String cmdClr(String type, boolean bold) {
        String s;
        switch (type.toLowerCase()) {
            case ("coords") -> s = "&6";
            case ("death") -> s = "&4";
            case ("ivan") -> s = "&B";
            case ("smp") -> s = "&#f50057";
            case ("wand") -> s = "&D";
            default -> s = "&F";
        }
        if (bold) s += "&L";
        return s;
    }
}