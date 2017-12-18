package com.uddernetworks.mcbook.main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BookHighlighter {

    public Map<Integer, BookClass> highlightBooks(Player player) throws IOException {
        Map<Integer, BookClass> bookClasses = new HashMap<>();

        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getContents()[i];
            if (itemStack != null && itemStack.getType() == Material.WRITTEN_BOOK) {
                BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

                BookClass bookClass = new BookClass(bookMeta.getTitle(), itemStack, bookMeta.getPages()).highlight();

                bookMeta.setPages(bookClass.getPages());

                itemStack.setItemMeta(bookMeta);

                bookClasses.put(i, bookClass);
            }
        }

        return bookClasses;
    }

}
