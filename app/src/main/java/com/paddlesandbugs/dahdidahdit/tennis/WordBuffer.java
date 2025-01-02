/****************************************************************************
    Dahdidahdit - an Android Morse trainer
    Copyright (C) 2021-2025 Matthias Jordan <matthias@paddlesandbugs.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
****************************************************************************/

package com.paddlesandbugs.dahdidahdit.tennis;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class WordBuffer {

    private final StringBuilder buffer = new StringBuilder();

    private HashMap<String, String> match;

    private String fullPatternMatch;

    private boolean wordEnd = false;


    public WordBuffer() {
        this("");
    }


    public WordBuffer(String initial) {
        if ((initial != null) && !initial.isEmpty()) {
            addWord(initial);
        }
    }


    private static String trim(String in) {
        String out = in;
        while ((out.length() != 0) && (out.charAt(0) == ' ')) {
            out = out.substring(1);
        }
        int l = out.length();
        while (((l = out.length()) != 0) && (out.charAt(l - 1) == ' ')) {
            out = out.substring(0, l - 1);
        }
        return out;
    }


    private void handleWordEnd() {
        if (wordEnd && (buffer.length() != 0)) {
            buffer.append(' ');
            wordEnd = false;
        }
    }


    public void addWord(String word) {
        handleWordEnd();
        buffer.append(word);
        endWord();
    }


    public void addChar(String c) {
        handleWordEnd();
        buffer.append(c);
    }


    public void endWord() {
        wordEnd = true;
    }


    public String getAndClear() {
        String tmp = buffer.toString();
        buffer.setLength(0);
        wordEnd = false;
        return tmp;
    }


    public String get() {
        return buffer.toString();
    }


    private String[] split(String s) {
        int i = s.lastIndexOf(' ');
        if (i == -1) {
            return new String[]{"", s};
        }
        String token = s.substring(i + 1);
        String rest = s.substring(0, i);
        return new String[]{rest, token};
    }


    /*
     * Patterns can use the wildcard # for "any word". If all # tokens in the buffer are equal,
     * the token is returned. Else the empty string is returned.
     *
     * The comparison is executed from the right (i.e. most recently keyed tokens first).
     *
     * Example:
     * Buffer contains "cq de w1aw", pattern is "cq de #", returns "w1aw".
     * Buffer contains "cq de w1aw w1aw", pattern is cq de # #", returns "w1aw".
     * Buffer contains "cq de w1aw w1aa", pattern is cq de # #", returns "".
     */
    public boolean matches(String pattern) {
        match = null;
        fullPatternMatch = null;
        String tmppat = pattern;
        String tmpbuf = buffer.toString();
        HashMap<String, String> wildcardContent = new HashMap<>();
        do {
            String[] p = split(tmppat);
            String[] b = split(tmpbuf);
            if (p[1].startsWith("#")) {
                String wildcardName = p[1].substring(1);
                String wcc = b[1];

                if (!textOK(wcc)) {
                    return false;
                }

                if (wildcardContent.get(wildcardName) == null) {
                    // Found wildcard name, store for later comparison or retrieval
                    wildcardContent.put(wildcardName, wcc);
                } else if (!wcc.equals(wildcardContent.get(wildcardName))) {
                    // Found wildcard but it does not with a previously stored copy
                    return false;
                }
            } else if (!p[1].equals(b[1])) {
                // word mismatch
                return false;
            }
            tmppat = p[0];
            tmpbuf = b[0];
        } while ((!tmppat.equals("")) && (!tmpbuf.equals("")));

        if ((!tmppat.equals("")) && (tmpbuf.equals(""))) {
            return false;
        } else {
            match = wildcardContent;
            fullPatternMatch = trim(buffer.substring(tmpbuf.length()));
            return true;
        }
    }


    private boolean textOK(String s) {
        int i = s.indexOf('*');
        return (i == -1);
    }


    /**
     * @return the part in the buffer that matches the "#". Call after {@link #matches(String)}.
     */
    public String getMatch() {
        return getMatch("");
    }


    public String getMatch(String num) {
        if (match == null) {
            return null;
        }

        return match.get(num);
    }


    /**
     * @return the part of the buffer that matches the entire pattern. Call after {@link #matches(String)}.
     */
    public String getFullPatternMatch() {
        return fullPatternMatch;
    }


    /**
     * Compares the buffer with the given string.
     *
     * @param other the string to compare the buffer with
     *
     * @return true, if they equal. Else false.
     */
    boolean equals(String other) {
        return buffer.toString().equals(other);
    }


    @NonNull
    @Override
    public String toString() {
        return buffer.toString();
    }
}
