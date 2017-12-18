package com.uddernetworks.mcbook.main;

public class StringUtils {

    public static String convertTabsToSpaces(String line, int tabWidth) {
        StringBuilder result = new StringBuilder();

        synchronized (result) {
            int tab_index = -1;
            int last_tab_index = 0;
            int added_chars = 0;
            int tab_size;
            while ((tab_index = line.indexOf("\t", last_tab_index)) != -1) {
                tab_size = tabWidth - ((tab_index + added_chars) % tabWidth);
                if (0 == tab_size) {
                    tab_size = tabWidth;
                }
                added_chars += tab_size - 1;
                result.append(line.substring(last_tab_index, tab_index));
                result.append(StringUtils.repeat(" ", tab_size));
                last_tab_index = tab_index + 1;
            }
            if (0 == last_tab_index) {
                return line;
            } else {
                result.append(line.substring(last_tab_index));
            }
        }

        return result.toString();
    }

    public static String repeat(String source, int count) {
        if (null == source) {
            return null;
        }

        StringBuilder new_string = new StringBuilder();
        synchronized (new_string) {
            while (count > 0) {
                new_string.append(source);
                count--;
            }

            return new_string.toString();
        }
    }

}
