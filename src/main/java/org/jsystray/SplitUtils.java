package org.jsystray;

import java.util.ArrayList;
import java.util.List;

public class SplitUtils {

    public static String[] split(String s) {
        boolean inString = false;
        StringBuilder str = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                str.append(c);
                if (i < s.length() - 1) {
                    str.append(s.charAt(++i));
                }
            } else if (c == ' ') {
                if (inString) {
                    str.append(c);
                } else {
                    if( !str.isEmpty()) {
                        list.add(str.toString());
                    }
                    str.setLength(0);
                }
            } else if (c == '"') {
                if (!inString) {
                    inString = true;
                    str.append(c);
                } else {
                    inString = false;
                    str.append(c);
                }
            } else {
                str.append(c);
            }
        }
        if (inString) {
            throw new IllegalArgumentException("Unclosed string");
        }
        if (!str.isEmpty()) {
            list.add(str.toString());
        }
        return list.toArray(new String[0]);
    }
}
