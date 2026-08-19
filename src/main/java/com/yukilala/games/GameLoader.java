package com.yukilala.games;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal JSON loader for games.json that avoids external dependencies.
 * This is intentionally lightweight and "good enough" for the simple array-of-objects format used here.
 */
public class GameLoader {

    private static final Pattern NAME_RE = Pattern.compile("\"name\"\s*:\s*\"([^\"]*)\"");
    private static final Pattern PACKAGE_RE = Pattern.compile("\"package\"\s*:\s*\"([^\"]*)\"");
    private static final Pattern NOTES_RE = Pattern.compile("\"notes\"\s*:\s*\"([^\"]*)\"");

    public static List<Game> loadFromResource() throws IOException {
        InputStream in = GameLoader.class.getResourceAsStream("/games.json");
        if (in == null) return Collections.emptyList();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        String s = sb.toString().trim();
        if (!s.startsWith("[")) return Collections.emptyList();
        // remove outer brackets
        s = s.substring(1, s.length() - 1).trim();
        if (s.isEmpty()) return Collections.emptyList();

        // split objects by '},' boundary (naive but OK for the simple format)
        String[] objs = s.split("\},\\s*\{");
        List<Game> result = new ArrayList<>();
        for (String obj : objs) {
            String o = obj.trim();
            if (!o.startsWith("{")) o = "{" + o;
            if (!o.endsWith("}")) o = o + "}";

            String name = matchOrNull(NAME_RE, o);
            String pkg = matchOrNull(PACKAGE_RE, o);
            String notes = matchOrNull(NOTES_RE, o);

            if (name != null || pkg != null || notes != null) {
                Game g = new Game(name, pkg, notes);
                result.add(g);
            }
        }
        return result;
    }

    private static String matchOrNull(Pattern p, String s) {
        Matcher m = p.matcher(s);
        if (m.find()) return m.group(1);
        return null;
    }
}
