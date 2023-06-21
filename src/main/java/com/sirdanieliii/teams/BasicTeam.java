package com.sirdanieliii.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.Utilities.replaceStr;
import static com.sirdanieliii.teams.Utilities.translateMsgClr;
import static com.sirdanieliii.teams.configuration.ConfigManager.*;
import static com.sirdanieliii.teams.events.Scoreboards.scoreboard;

public class BasicTeam {
    private final Scoreboard board;
    private final int number; // This is the team name
    private final ChatColor colour;
    private Team team;

    public BasicTeam(Scoreboard board, int number) {
        this.board = board;
        this.number = number;
        colour = getRandomColour();
        register();
    }

    public BasicTeam(Scoreboard board, int number, ChatColor colour) {
        this.board = board;
        this.number = number;
        this.colour = colour;
        register();
    }

    @Override
    public String toString() {
        return colour + "Team " + number;
    }

    /**
     * Determine if an entry in teams.yml is valid or not (use this when loading teams from a config)
     *
     * @param key team name
     * @return true if valid, false otherwise
     */
    public static boolean validConfig(String key) {
        try {
            int number = Integer.parseInt(key); // See if key is a number
            String clr = configTeams.getConfig().getString(String.format("teams.%s.colour", key));
            if (clr != null && number != 0) return ChatColor.getByChar(clr) != null; // Validate colour from string
        } catch (NullPointerException | NumberFormatException ignored) {
        }
        return false; // Return false by default (or if clr == null)
    }

    private void register() {
        team = board.registerNewTeam(String.valueOf(number));
        team.setColor(colour);
        team.setPrefix(colour + "[" + ChatColor.WHITE + number + colour + "]");
        pluginTeams.put(number, this);
        addTeamToConfig(this);
    }

    public void disband(Player player) {
        // Send message
        List<String> players = configTeams.getConfig().getStringList(String.format("teams.%s.players", getNumber()));
        for (String i : players) {
            Player recipient = Bukkit.getPlayer(i);
            if (recipient == null) continue;
            recipient.sendMessage(translateMsgClr(cmdHeader) + " " + replaceStr(messages.get("disbanded_team"),
                    "{player}", player.getName(), "{team}", toString()));
            pluginPlayerData.put(recipient, null);
        }
        // Remove team
        team.unregister();
        pluginTeams.remove(number);
        configTeams.deleteKey(String.format("teams.%s.colour", getNumber()));
        getThisPlugin().getLogger().info(String.format("Removed %s from teams.yml", this));
    }

    public void addPlayer(boolean addToConf, Player... players) {
        for (Player p : players) {
            team.addEntry(p.getUniqueId().toString());
            pluginPlayerData.put(p, this);
            if (addToConf) {
                addToConfigStrList(String.format("teams.%s.players", number), p.getUniqueId().toString());
            }
        }
    }

    public void removePlayer(Player player) {
        team.removeEntry(player.getUniqueId().toString());
        pluginPlayerData.put(player, null);
        removeFromConfigStrList(String.format("teams.%d.players", getNumber()), player.getUniqueId().toString());
    }

    public void announceMsg(String msg, Player... ignore) {
        // Send message
        List<String> players = configTeams.getConfig().getStringList(String.format("teams.%s.players", getNumber()));
        List<Player> ignoredPlayers = new ArrayList<>(List.of(ignore));
        for (String i : players) {
            Player recipient = Bukkit.getPlayer(i);
            if (recipient == null || ignoredPlayers.contains(recipient)) continue;
            recipient.sendMessage(translateMsgClr(msg));
        }
    }

    public int getNumber() {
        return number;
    }

    public ChatColor getColour() {
        return colour;
    }

    @Nullable
    public static ChatColor getChatColor(String msg) {
        if (msg.length() == 2) {
            if (msg.charAt(0) == '&') return ChatColor.getByChar(msg.charAt(1));
        }
        return switch (msg.toUpperCase()) {
            case "BLACK" -> ChatColor.BLACK;
            case "DARK_BLUE" -> ChatColor.DARK_BLUE;
            case "DARK_GREEN" -> ChatColor.DARK_GREEN;
            case "DARK_AQUA" -> ChatColor.DARK_AQUA;
            case "DARK_RED" -> ChatColor.DARK_RED;
            case "DARK_PURPLE" -> ChatColor.DARK_PURPLE;
            case "GOLD" -> ChatColor.GOLD;
            case "GRAY" -> ChatColor.GRAY;
            case "DARK_GRAY" -> ChatColor.DARK_GRAY;
            case "BLUE" -> ChatColor.BLUE;
            case "GREEN" -> ChatColor.GREEN;
            case "AQUA" -> ChatColor.AQUA;
            case "RED" -> ChatColor.RED;
            case "LIGHT_PURPLE" -> ChatColor.LIGHT_PURPLE;
            case "YELLOW" -> ChatColor.YELLOW;
            case "WHITE" -> ChatColor.WHITE;
            default -> null;
        };
    }

    public static ChatColor getRandomColour() {
        return switch (new Random().nextInt(0, 15)) {
            case 0 -> ChatColor.BLACK;
            case 1 -> ChatColor.DARK_BLUE;
            case 2 -> ChatColor.DARK_GREEN;
            case 3 -> ChatColor.DARK_AQUA;
            case 4 -> ChatColor.DARK_RED;
            case 5 -> ChatColor.DARK_PURPLE;
            case 6 -> ChatColor.GOLD;
            case 7 -> ChatColor.GRAY;
            case 8 -> ChatColor.DARK_GRAY;
            case 9 -> ChatColor.BLUE;
            case 10 -> ChatColor.GREEN;
            case 11 -> ChatColor.AQUA;
            case 12 -> ChatColor.RED;
            case 13 -> ChatColor.LIGHT_PURPLE;
            case 14 -> ChatColor.YELLOW;
            default -> ChatColor.WHITE;
        };
    }

    public static void addTeamToConfig(BasicTeam team) {
        configTeams.getConfig().set(String.format("teams.%s.colour", team.getNumber()), team.getColour().name());
        configTeams.getConfig().set(String.format("teams.%s.players", team.getNumber()), new ArrayList<>());
        configTeams.save();
        getThisPlugin().getLogger().info(String.format("Added Team %s to teams.yml", team.getNumber()));
    }

    public static int parseNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    public static int numberOfTeams() {
        return pluginTeams.size();
    }

    public static List<Integer> getAllTeamsOrdered() {
        List<Integer> numbers = new ArrayList<>(pluginTeams.keySet());
        Collections.sort(numbers);
        return numbers;
    }

    public static int getNextTeamNumber() {
        List<Integer> numbers = getAllTeamsOrdered();
        int n = numbers.size() + 1; // Including the missing number
        int expectedSum = (n * (n + 1)) / 2;
        int actualSum = 0;
        for (int number : numbers) {
            actualSum += number;
        }
        return expectedSum - actualSum;
    }

    public static void addToConfigStrList(String path, String entry) {
        List<String> lst = configTeams.getConfig().getStringList(path);
        lst.add(entry);
        configTeams.getConfig().set(path, lst);
        configTeams.save();
    }

    public static void removeFromConfigStrList(String path, String entry) {
        List<String> lst = configTeams.getConfig().getStringList(path);
        lst.remove(entry);
        configTeams.getConfig().set(path, lst);
        configTeams.save();
    }

    @Nullable
    public static BasicTeam detectPlayerTeam(Player player) {
        try {
            for (String num : Objects.requireNonNull(configTeams.getConfig().getConfigurationSection("teams")).getKeys(false)) {
                if (validConfig(num)) {
                    List<String> players = configTeams.getConfig().getStringList(String.format("teams.%s.players", num));
                    for (String uuid : players) {
                        if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                            return new BasicTeam(scoreboard,
                                    Integer.parseInt(num),
                                    getChatColor(Objects.requireNonNull(configTeams.getConfig().getString(String.format("teams.%s.colour", num)))));
                        }
                    }
                }
            }
        } catch (NullPointerException ignored) {
        }
        return null;
    }
}
