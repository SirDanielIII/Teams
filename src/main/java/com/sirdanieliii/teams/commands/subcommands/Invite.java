package com.sirdanieliii.teams.commands.subcommands;

import com.sirdanieliii.teams.commands.SubCommand;
import com.sirdanieliii.teams.BasicTeam;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static com.sirdanieliii.teams.Utilities.*;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;

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
        if (!(player.hasPermission("teams.invite"))) {
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
        } else { // teams invite [players]
            for (String i : Arrays.copyOfRange(args, 1, args.length)) {
                Player recipient = Bukkit.getPlayer(i);
                if (recipient == null) continue;
                TextComponent msg = translateMsgClrComponent(String.format(
                        cmdHeader + " " + "&F%s has invited you to %s. \n&7>>> &EClick on this message to join!", player.getName(), team));
                msg.setClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND, messages.get("teams_join").replace("[number]", String.valueOf(team.getNumber()))));
                msg.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, new Text(translateMsgClr("&7Click on this message to join!"))));
                recipient.spigot().sendMessage(msg);
                player.sendMessage(replaceStr(cmdHeader + " " + messages.get("invite_success"), "{player}", recipient.getName()));
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
