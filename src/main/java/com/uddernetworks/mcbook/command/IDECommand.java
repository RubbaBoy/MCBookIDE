package com.uddernetworks.mcbook.command;

import com.uddernetworks.command.Argument;
import com.uddernetworks.command.ArgumentError;
import com.uddernetworks.command.ArgumentList;
import com.uddernetworks.command.Command;
import com.uddernetworks.mcbook.main.BookClass;
import com.uddernetworks.mcbook.main.BookCompiler;
import com.uddernetworks.mcbook.main.BookHighlighter;
import com.uddernetworks.mcbook.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;

@Command(name = "bookcompile", aliases={"bc", "cb", "bookc", "compileb", "compilebook"}, consoleAllow = false, minArgs = 1, maxArgs = 1)
public class IDECommand {

    private Main main;

    public IDECommand(Main main) {
        this.main = main;
    }

    @Argument(format = "highlight")
    public void highlight(CommandSender sender, ArgumentList args) {
        final long start = System.currentTimeMillis();
        Player player = (Player) sender;
        boolean hadErrors = false;
        try {
            new BookHighlighter().highlightBooks(player);
        } catch (IOException e) {
            e.printStackTrace();
            hadErrors = true;
        }

        long diff = System.currentTimeMillis() - start;

        if (hadErrors) {
            player.sendMessage(ChatColor.RED + "Finished with errors in " + ChatColor.GOLD + diff + ChatColor.RED + "ms");
        } else {
            player.sendMessage(ChatColor.GOLD + "Highlighted successfully in " + ChatColor.RED + diff + ChatColor.GOLD + "ms");
        }
    }

    @Argument(format = "compile")
    public void compile(CommandSender sender, ArgumentList args) {
        final long start = System.currentTimeMillis();
        Player player = (Player) sender;
        boolean hadErrors;
        try {
            Map<Integer, BookClass> books = new BookHighlighter().highlightBooks(player);

            BookCompiler bookCompiler = main.getBookCompiler();
            hadErrors = bookCompiler.compileBooks(books, player, false);

            player.updateInventory();
        } catch (IOException e) {
            e.printStackTrace();
            hadErrors = true;
        }

        long diff = System.currentTimeMillis() - start;

        if (hadErrors) {
            player.sendMessage(ChatColor.RED + "Finished with errors in " + ChatColor.GOLD + diff + ChatColor.RED + "ms");
        } else {
            player.sendMessage(ChatColor.GOLD + "Compiled successfully in " + ChatColor.RED + diff + ChatColor.GOLD + "ms");
        }
    }

    @Argument(format = "execute")
    public void execute(CommandSender sender, ArgumentList args) {
        final long start = System.currentTimeMillis();
        Player player = (Player) sender;
        boolean hadErrors;
        try {
            Map<Integer, BookClass> books = new BookHighlighter().highlightBooks(player);

            BookCompiler bookCompiler = main.getBookCompiler();
            hadErrors = bookCompiler.compileBooks(books, player, true);

            player.updateInventory();
        } catch (IOException e) {
            e.printStackTrace();
            hadErrors = true;
        }

        long diff = System.currentTimeMillis() - start;

        if (hadErrors) {
            player.sendMessage(ChatColor.RED + "Finished with errors in " + ChatColor.GOLD + diff + ChatColor.RED + "ms");
        } else {
            player.sendMessage(ChatColor.GOLD + "Executed successfully in " + ChatColor.RED + diff + ChatColor.GOLD + "ms");
        }
    }

    @ArgumentError
    public void argumentError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + "Error while executing command: " + message);
    }
}
