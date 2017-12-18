package com.uddernetworks.mcbook.highlighter;

import com.uddernetworks.mcbook.main.StringUtils;

import java.io.*;

public class CustomJavaRenderer {

    private String getCssClass(int style) {
        switch (style) {
            case 1:
                return "0";
            case 2:
                return "0";
            case 3:
                return "1";
            case 4:
                return "2";
            case 5:
                return "1";
            case 6:
                return "4";
            case 7:
                return "7";
            case 8:
                return "7";
            case 9:
                return "7";
            default:
                return null;
        }
    }

    public String highlight(String text) throws IOException {
        JavaHighlighter highlighter = this.getHighlighter();

        InputStream is = new ByteArrayInputStream(text.getBytes());
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader r = new BufferedReader(isr);

        StringBuilder builder = new StringBuilder();

        String line;
        char[] token;
        int length;
        int style;
        String css_class;
        int previous_style = 0;
        boolean newline = false;
        while ((line = r.readLine()) != null) {
            line = StringUtils.convertTabsToSpaces(line, 4);

            Reader lineReader = new StringReader(line);
            highlighter.setReader(lineReader);
            int index = 0;
            while (index < line.length()) {
                style = highlighter.getNextToken();
                length = highlighter.getTokenLength();
                token = line.substring(index, index + length).toCharArray();


                if (style != previous_style || newline) {
                    css_class = getCssClass(style);

                    if (css_class != null) {
                        builder.append("§").append(css_class);
                    }

                    previous_style = style;
                }

                newline = false;
                builder.append(token);

                index += length;
            }

            newline = true;
            builder.append("\n");
        }

        return builder.toString();
    }

    private JavaHighlighter getHighlighter() {
        JavaHighlighter highlighter = new JavaHighlighter();
        JavaHighlighter.ASSERT_IS_KEYWORD = true;
        return highlighter;
    }
}
