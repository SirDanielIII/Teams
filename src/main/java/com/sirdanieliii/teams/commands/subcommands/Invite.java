package com.sirdanieliii.teams.commands.subcommands.teams;

import com.sirdanieliii.teams.commands.SubCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.Utilities.translateMsgClrComponent;
import static com.sirdanieliii.teams.commands.CommandManager.cmdHeader;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;

public class Invite extends SubCommand {
    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getCmdGroup() {
        return "teams";
    }

    @Override
    public String getDescription() {
        return "Invites a player to join a team";
    }

    @Override
    public String getSyntax() {
        return messages.get("teams_invite");
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
                TextComponent msg = translateMsgClrComponent(
                        String.format("&E&LALERT! &F%s has invited you to Team %d. \n&A>>> Click on this message to join!", player.getName(), teamNumber));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, messages.replace("[number]", String.valueOf(teamNumber))));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translateMsgClr("&7Click on this message to join!"))));
                recipient.spigot().sendMessage(msg);
                player.sendMessage(translateMsgClr(cmdHeader("teams") + "Invite successfully sent to " + recipient.getName()));
            } catch (NullPointerException | NumberFormatException ignored) {
            }
        }
        return true;
    }

    @Override
    public List<String> getSubcommandArgs(Player player, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length >= 2) {
            Collection<? extends Player> playerLst = Bukkit.getOnlinePlayers();
            for (String i : args) {
                for (Player p : playerLst) {
                    if (p.getName().startsWith(i)) list.add(p.getName());
                }
            }
        }
        return list;
    }
}
