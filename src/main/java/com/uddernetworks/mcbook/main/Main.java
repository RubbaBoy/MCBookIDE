package com.uddernetworks.mcbook.main;

import com.uddernetworks.command.CommandManager;
import com.uddernetworks.config.Config;
import com.uddernetworks.mcbook.command.IDECommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private BookCompiler bookCompiler;

    @Override
    public void onEnable() {
        CommandManager manager = new CommandManager();
        manager.registerCommand(this, new IDECommand(this));

        Config.getDefaultOptions()
                .enableAutoReload(true)
                .enableAutoSave(true)
                .setDefaultLocation(getDataFolder());

        bookCompiler = new BookCompiler(this);

        Config.registerAnnotatedClass(bookCompiler);
    }

    public BookCompiler getBookCompiler() {
        return bookCompiler;
    }
}
