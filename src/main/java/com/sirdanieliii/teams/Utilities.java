package com.sirdanieliii.teams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

import static java.lang.Character.isAlphabetic;

public class Utilities {

    public static String replaceStr(String msg, String toReplace, String replace) {
        // Change method so that it specifies what thing to replace
        return translateMsgClr(msg.replace(toReplace, replace));
    }

    public static TextComponent replaceStr(String msg, String toReplace, TextComponent replace) {
        TextComponent s = translateMsgClrComponent(msg.substring(0, msg.indexOf(toReplace)));
        s.addExtra(replace); // Component
        s.addExtra(translateMsgClrComponent(msg.substring(msg.indexOf(toReplace) + toReplace.length())));
        return s;
    }

    public static String replaceStr(String msg, String toReplace1, String replace1, String toReplace2, String replace2) {
        return translateMsgClr(msg.replace(toReplace1, replace1).replace(toReplace2, replace2));
    }

    public static TextComponent replaceStr(String msg, String toReplace1, TextComponent replace1, String toReplace2, TextComponent replace2) {
        TextComponent s = translateMsgClrComponent(msg.substring(0, msg.indexOf(toReplace1)));
        ChatColor c = s.getColor();
        TextComponent part1 = translateMsgClrComponent(msg.substring(msg.indexOf(toReplace1) + toReplace1.length() + 1, msg.indexOf(toReplace2)));
        part1.setColor(c);
        TextComponent part2 = translateMsgClrComponent(msg.substring(msg.indexOf(toReplace2) + toReplace2.length()));
        part2.setColor(c);
        s.addExtra(replace1);
        s.addExtra(part1);
        s.addExtra(replace2);
        s.addExtra(part2);
        return s;
    }

    public static String translateMsgClr(String msg) {
        // Original code by Kody Simpson -> https://gitlab.com/kody-simpson/spigot/1.16-color-translator/-/blob/master/ColorUtils.java
        String[] texts = msg.split(String.format("((?<=%1$s)|(?=%1$s))", "&"));
        StringBuilder finalMsg = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                i++; // Get the next string
                if (texts[i].charAt(0) == '#') finalMsg.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                else finalMsg.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
            } else finalMsg.append(texts[i]);
        }
        return finalMsg.toString();
    }

    public static TextComponent translateMsgClrComponent(String text) {
        /*
         Same result as translateMsgClr(), but for TextComponents instead since hex codes need to be implemented differently
         Original code by Kody Simpson -> https://gitlab.com/kody-simpson/spigot/1.16-color-translator/-/blob/master/ColorUtils.java
         */
        String[] texts = text.split(String.format("((?<=%1$s)|(?=%1$s))", "&"));
        ComponentBuilder builder = new ComponentBuilder();
        for (int i = 0; i < texts.length; i++) {
            TextComponent subComponent = new TextComponent();
            if (texts[i].equalsIgnoreCase("&")) {
                // Get the next string
                i++;
                if (texts[i].charAt(0) == '#') {
                    subComponent.setText(texts[i].substring(7));
                    subComponent.setColor(ChatColor.of(texts[i].substring(0, 7)));
                    builder.append(subComponent);
                } else {
                    if (texts[i].length() > 1) {
                        subComponent.setText(texts[i].substring(1));
                    } else {
                        subComponent.setText(" ");
                    }
                    switch (Character.toLowerCase(texts[i].charAt(0))) {
                        case '0' -> subComponent.setColor(ChatColor.BLACK);
                        case '1' -> subComponent.setColor(ChatColor.DARK_BLUE);
                        case '2' -> subComponent.setColor(ChatColor.DARK_GREEN);
                        case '3' -> subComponent.setColor(ChatColor.DARK_AQUA);
                        case '4' -> subComponent.setColor(ChatColor.DARK_RED);
                        case '5' -> subComponent.setColor(ChatColor.DARK_PURPLE);
                        case '6' -> subComponent.setColor(ChatColor.GOLD);
                        case '7' -> subComponent.setColor(ChatColor.GRAY);
                        case '8' -> subComponent.setColor(ChatColor.DARK_GRAY);
                        case '9' -> subComponent.setColor(ChatColor.BLUE);
                        case 'a' -> subComponent.setColor(ChatColor.GREEN);
                        case 'b' -> subComponent.setColor(ChatColor.AQUA);
                        case 'c' -> subComponent.setColor(ChatColor.RED);
                        case 'd' -> subComponent.setColor(ChatColor.LIGHT_PURPLE);
                        case 'e' -> subComponent.setColor(ChatColor.YELLOW);
                        case 'f' -> subComponent.setColor(ChatColor.WHITE);
                        case 'k' -> subComponent.setObfuscated(true);
                        case 'l' -> subComponent.setBold(true);
                        case 'm' -> subComponent.setStrikethrough(true);
                        case 'n' -> subComponent.setUnderlined(true);
                        case 'o' -> subComponent.setItalic(true);
                        case 'r' -> subComponent.setColor(ChatColor.RESET);
                    }
                    builder.append(subComponent);
                }
            } else {
                builder.append(texts[i]);
            }
        }
        return new TextComponent(builder.create());
    }

    public static String cleanStrForYMLKey(String arg) {
        StringBuilder finalStr = new StringBuilder();
        for (Character c : arg.toCharArray()) {
            if (isAlphabetic(c) || Arrays.asList('_', '-', '\'').contains(c) || Character.isDigit(c)) {
                finalStr.append(c);
            }
        }
        if (finalStr.length() != 0) return finalStr.toString();
        else return null;
    }
}
