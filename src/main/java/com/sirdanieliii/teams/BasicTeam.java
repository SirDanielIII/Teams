package com.sirdanieliii.teams.events;

import com.sirdanieliii.teams.commands.configuration.ConfigYML;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.sirdanieliii.teams.Teams.getThisPlugin;
import static com.sirdanieliii.teams.commands.configuration.ConfigManager.*;
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
     * Determine if an entry in teams.yml is valid or not by checking its colour value
     *
     * @param key team name
     * @return true if valid, false otherwise
     */
    public static boolean validConfig(String key) {
        FileConfiguration config = configTeams.getConfig();
        try {
            Integer.parseInt(key); // See if key is a number
            String clr = config.getString(String.format("teams.%s.colour", key));
            if (clr != null) return ChatColor.getByChar(clr) != null; // Validate colour from string
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

    public void remove() {
        team.unregister();
        pluginTeams.remove(number);
        removeTeamFromConfig(this);
    }

    public void addPlayer(boolean addToConf, Player... players) {
        for (Player p : players) {
            team.addEntry(p.getName());
            pluginPlayerData.put(p, this);
            if (addToConf) addToConfigStrList(configTeams, String.format("teams.%s.players", number), p.getUniqueId().toString());
        }
    }

    public int getNumber() {
        return number;
    }

    public ChatColor getColour() {
        return colour;
    }

    public Team getTeam() {
        return team;
    }

    @Nullable
    public static ChatColor getChatColor(String msg) {
        if (msg.length() == 2) {
            if (msg.charAt(0) == '&') return ChatColor.getByChar(msg.charAt(1));
        }
        return ChatColor.getByChar(msg.toUpperCase()); // Matches enum name
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
        FileConfiguration config = configTeams.getConfig();
        config.set(String.format("teams.%s.colour", team.getNumber()), team.getColour().name());
        config.set(String.format("teams.%s.players", team.getNumber()), new ArrayList<>());
        getThisPlugin().getLogger().info(String.format("Added Team %s to teams.yml", team.getNumber()));
    }

    public static void removeTeamFromConfig(BasicTeam team) {
        configTeams.deleteKey(String.format("teams.%s.colour", team.getNumber()));
        getThisPlugin().getLogger().info(String.format("Removed Team %s from teams.yml", team.getNumber()));
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
        for (int i = 0; i < numbers.size() - 1; i++) {
            if (numbers.get(i) + 1 != numbers.get(i + 1)) {
                return numbers.get(i) + 1;
            }
        }
        return 1;
    }

    public static void addToConfigStrList(ConfigYML config, String path, String entry) {
        List<String> lst = config.getConfig().getStringList(path);
        lst.add(entry);
        config.getConfig().set(path, lst);
    }

    @Nullable
    public static BasicTeam detectPlayerTeam(Player player) {
        for (String num : Objects.requireNonNull(configTeams.getConfig().getConfigurationSection("teams")).getKeys(false)) {
            List<String> players = configTeams.getConfig().getStringList(String.format("teams.%s.players", num));
            for (String uuid : players) {
                if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                    return new BasicTeam(scoreboard,
                            Integer.parseInt(num),
                            getChatColor(Objects.requireNonNull(configTeams.getConfig().getString(String.format("teams.%s.colour", num)))));
                }
            }
        }
        return null;
    }
}
