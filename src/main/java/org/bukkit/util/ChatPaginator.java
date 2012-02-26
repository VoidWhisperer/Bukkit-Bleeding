package org.bukkit.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The ChatPaginator takes a raw string of arbitrary length and breaks it down into an array of strings appropriate
 * for displaying on the Minecraft player console.
 */
public class ChatPaginator {
    public static final int DEFAULT_CHAT_WIDTH = 54;
    public static final int DEFAULT_PAGE_HEIGHT = 20;
    public static final int UNBOUNDED_PAGE_HEIGHT = Integer.MAX_VALUE;

    /**
     * Breaks a raw string up into pages using the default width and height.
     * @param unpaginatedString The raw string to break.
     * @param pageNumber The page number to fetch.
     * @return A single chat page.
     */
    public static ChatPage paginate(String unpaginatedString, int pageNumber) {
        return  paginate(unpaginatedString, pageNumber, DEFAULT_CHAT_WIDTH, DEFAULT_PAGE_HEIGHT);
    }

    /**
     * Breaks a raw string up into pages using a provided width and height.
     * @param unpaginatedString The raw string to break.
     * @param pageNumber The page number to fetch.
     * @param lineLength The desired width of a chat line.
     * @param pageHeight The desired number of lines in a page.
     * @return A single chat page.
     */
    public static ChatPage paginate(String unpaginatedString, int pageNumber, int lineLength, int pageHeight) {
        String[] lines = wordWrap(unpaginatedString, lineLength);

        int totalPages = lines.length / pageHeight + (lines.length % pageHeight == 0 ? 0 : 1);
        int actualPageNumber = pageNumber <= totalPages ? pageNumber : totalPages;

        int from = (actualPageNumber - 1) * pageHeight;
        int to = from + pageHeight <= lines.length  ? from + pageHeight : lines.length;
        String[] selectedLines = Arrays.copyOfRange(lines, from, to);
        
        return new ChatPage(selectedLines, actualPageNumber, totalPages);
    }

    /**
     * Breaks a raw string up into a series of lines. Words are wrapped using spaces as decimeters and the newline
     * character is respected.
     * @param rawString The raw string to break.
     * @param lineLength The length of a line of text.
     * @return An array of word-wrapped lines.
     */
    public static String[] wordWrap(String rawString, int lineLength) {
        // A null string is a single line
        if (rawString == null) {
            return new String[] {""};
        }
        
        // A string shorter than the lineWidth is a single line
        if (rawString.length() <= lineLength) {
            return new String[] {rawString};
        }

        char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<String>();

        for (char c : rawChars) {
            if (c == ' ' || c == '\n') {
                if (line.length() == 0 && word.length() > lineLength) { // special case: extremely long word begins a line
                    for (String partialWord : word.toString().split("(?<=\\G.{" + lineLength + "})")) {
                        lines.add(partialWord);
                    }
                } else if (line.length() + word.length() ==  lineLength) { // Line exactly the correct length...newline
                    line.append(word);
                    lines.add(line.toString());
                    line = new StringBuilder();
                } else if (line.length() + 1 + word.length() > lineLength) { // Line too long...break the line
                    for (String partialWord : word.toString().split("(?<=\\G.{" + lineLength + "})")) {
                        lines.add(line.toString());
                        line = new StringBuilder(partialWord);
                    }
                } else {
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                }
                word = new StringBuilder();
                
                if (c == '\n') { // Newline forces the line to flush
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
            } else {
                word.append(c);
            }
        }

        if(line.length() > 0) { // Only add the last line if there is anything to add
            lines.add(line.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    public static class ChatPage {
        
        private String[] lines;
        private int pageNumber;
        private int totalPages;
        
        public ChatPage(String[] lines, int pageNumber, int totalPages) {
            this.lines = lines;
            this.pageNumber = pageNumber;
            this.totalPages = totalPages;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public String[] getLines() {

            return lines;
        }
    }
}
