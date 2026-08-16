package cloudy.autume.addition.inventory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses only received Century Cake refresh messages. */
final class CenturyCakeParser {
    private static final Pattern FORMATTING_CODE = Pattern.compile("§.");
    private static final Pattern REFRESH = Pattern.compile(
            "^Big Yum! You (?:refresh|gain) (.+) for 48 hours!$", Pattern.CASE_INSENSITIVE);

    private CenturyCakeParser() {
    }

    static CenturyCakeCatalog.Cake parse(String raw) {
        if (raw == null) return null;
        String plain = FORMATTING_CODE.matcher(raw).replaceAll("").trim();
        Matcher matcher = REFRESH.matcher(plain);
        if (!matcher.matches()) return null;
        return CenturyCakeCatalog.instance().matchEffectText(matcher.group(1));
    }
}
