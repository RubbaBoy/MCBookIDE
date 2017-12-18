package com.uddernetworks.mcbook.main;

import com.uddernetworks.mcbook.highlighter.CustomJavaRenderer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookClass {

    private String name;
    private ItemStack item;
    private String code;
    private List<String> pages;
    private List<String> preHighlightPages;

    public BookClass(String name, ItemStack item, List<String> pages) {
        this.name = name;
        this.item = item;
        this.pages = pages;

        StringBuilder codeBuilder = new StringBuilder();

        for (String page : pages) {
            codeBuilder.append(ChatColor.stripColor(page)).append("\n");
        }

        this.code = codeBuilder.toString();
    }

    public BookClass highlight() throws IOException {
        CustomJavaRenderer customJavaRenderer = new CustomJavaRenderer();
        List<String> newPages = new ArrayList<>();
        List<String> preHighlight = new ArrayList<>();

        for (String page : pages) {
            page = ChatColor.stripColor(page);

            preHighlight.add(page);

            String highlightedPage = customJavaRenderer.highlight(page);
            newPages.add(highlightedPage);
        }

        pages = newPages;
        preHighlightPages = preHighlight;

        return this;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getCode() {
        return code;
    }

    public List<String> getPages() {
        return pages;
    }

    public List<String> getPreHighlightPages() {
        return preHighlightPages;
    }
}
