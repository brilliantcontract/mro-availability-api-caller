package bc.mro.mrosupply_com_api_caller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** Utility methods for cookie handling. */
public final class CookieUtils {
    private CookieUtils() {
    }

    /**
     * Parse cookies from a text blob copied from the browser.
     * Each line should be TAB separated name and value.
     * Quotes around the value are stripped.
     */
    public static Map<String, String> parseCookies(final String rawCookies) {
        Map<String, String> out = new LinkedHashMap<>();
        if (rawCookies == null) {
            return out;
        }
        Arrays.stream(rawCookies.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(line -> {
                    int tab = line.indexOf('\t');
                    if (tab <= 0) {
                        return;
                    }
                    String name = line.substring(0, tab).trim();
                    String value = line.substring(tab + 1).trim();
                    if (value.length() >= 2
                            && value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    out.put(name, value);
                });
        return out;
    }

    /**
     * Build a Cookie header value from the provided map.
     */
    public static String buildCookieHeader(Map<String, String> cookies) {
        return cookies.entrySet().stream()
                .map(e -> e.getKey() + '=' + e.getValue())
                .collect(Collectors.joining("; "));
    }
}
