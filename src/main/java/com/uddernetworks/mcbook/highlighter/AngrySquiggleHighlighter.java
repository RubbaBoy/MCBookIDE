package com.uddernetworks.mcbook.highlighter;

import com.uddernetworks.mcbook.main.BookClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AngrySquiggleHighlighter {

    private Map<BookClass, List<Diagnostic<? extends JavaFileObject>>> errors;
    private Player player;

    public AngrySquiggleHighlighter(Map<BookClass, List<Diagnostic<? extends JavaFileObject>>> errors, Player player) {
        this.errors = errors;
        this.player = player;
    }

    public void highlightAll() {
        for (BookClass bookClass : errors.keySet()) {
            List<Diagnostic<? extends JavaFileObject>> bookErrors = errors.get(bookClass);
            highlight(bookClass, bookErrors);
        }
    }

    private void highlight(BookClass bookClass, List<Diagnostic<? extends JavaFileObject>> bookErrors) {
        List<String> pages = bookClass.getPages();

        for (Diagnostic<? extends JavaFileObject> diagnostic : bookErrors) {
            String err = "Error in class " + (diagnostic.getSource().getName().substring(1).replace("/", ".")) + " [" + diagnostic.getLineNumber() + ":" + (diagnostic.getColumnNumber() == -1 ? "?" : diagnostic.getColumnNumber()) + "] " + diagnostic.getMessage(Locale.ENGLISH);
            System.out.println(err);
            player.sendMessage(ChatColor.RED + err);

            highlightLocation(pages, (int) diagnostic.getLineNumber(), (int) diagnostic.getColumnNumber(), (int) (diagnostic.getEndPosition() - diagnostic.getStartPosition()));
        }

        BookMeta bookMeta = (BookMeta) bookClass.getItem().getItemMeta();
        bookMeta.setPages(pages);

        bookClass.getItem().setItemMeta(bookMeta);

    }

    private void highlightLocation(List<String> pages, int lineNum, int columnNum, int underlineLength) {
        int totalLines = 0;
        lineNum--;
        underlineLength++;
        columnNum--;

        for (int i = 0; i < pages.size(); i++) {
            String page = pages.get(i);
            String[] lines = page.split("\n");
            if (lines.length + totalLines > lineNum) {
                lines[lineNum - totalLines] = forceUnderlineLength(lines[lineNum - totalLines], columnNum, underlineLength);

                pages.set(i, String.join("\n", lines));
                break;
            } else {
                totalLines += lines.length;
            }
        }
    }


    private static String forceUnderlineLength(String string, int startLoc, int underlineLength) {
        if (underlineLength == 0) underlineLength = string.length();

        String regex = "§([0-9]|[a-f])";
        String[] tempArr = string.split(regex);
        String[] rawPartArray = new String[tempArr.length - 1];
        System.arraycopy(tempArr, 1, rawPartArray, 0, tempArr.length - 1);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);

        List<String> parts = Arrays.asList(rawPartArray);
        List<String> codes = new ArrayList<>();

        while(matcher.find()) {
            codes.add(matcher.group(0));
        }

        StringBuilder builder = new StringBuilder();
        int length = 0;
        int index = 0;
        boolean underlined = false;
        boolean prevUnderline = false;
        for (String part : parts) {
            if (length + part.length() > startLoc && startLoc + underlineLength > length + part.length()) {

                char[] chars = part.toCharArray();

                if (codes.size() > index) builder.append(codes.get(index));

                for (int i = 0; i < chars.length; i++) {
                    if (length + i == startLoc) {
                        builder.append("§4§n").append(chars[i]);
                        prevUnderline = true;
                        underlined = true;
                    } else {
                        if (length + i >= startLoc + underlineLength && prevUnderline) {
                            builder.append("§r");
                            prevUnderline = false;
                        }
                        builder.append(chars[i]);
                    }
                }

                index++;
                length += part.length();
            } else {
                boolean start = true;
                char[] chars = part.toCharArray();

                for (int i = 0; i < chars.length; i++) {
                    if (length + i == startLoc) {
                        builder.append(chars[i]);
                        if (start) {
                            builder.append(codes.get(index)).append(("§4§n"));
                            start = false;
                        }
                        prevUnderline = true;
                        underlined = true;
                    } else {
                        if (length + i >= startLoc + underlineLength && prevUnderline) {
                            builder.append("§r");
                            prevUnderline = false;
                        }

                        if (start) {
                            builder.append(codes.get(index));
                            if (prevUnderline) builder.append(("§4§n"));
                            start = false;
                        }

                        builder.append(chars[i]);
                    }
                }

                index++;
                length += part.length();
            }

        }

        if (!underlined) {
            builder.append("§4§n §r");
        }

        return builder.toString();
    }

}
