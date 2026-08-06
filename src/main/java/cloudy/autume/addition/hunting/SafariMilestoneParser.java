package cloudy.autume.addition.hunting;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Pure parser for received Safari Milestones menu/tooltip text. */
public final class SafariMilestoneParser {
    private static final String BIOME = "(Cavern|Forest|Haunted|Icy)(?:\\s+Biome)?";
    private static final String LEVEL = "([IVX]{1,4}|10|[0-9])(?![0-9,])";
    private static final Pattern BIOME_NAME = Pattern.compile("(?i)\\b" + BIOME + "\\b");
    private static final Pattern BIOME_LEVEL = Pattern.compile(
            "(?i)\\b" + BIOME + "(?:\\s+(?:Safari\\s+)?Milestone|\\s+(?:Level|Tier))"
                    + "\\s*[:#\\-]?\\s*" + LEVEL + "\\b");
    private static final Pattern CONTEXT_LEVEL = Pattern.compile(
            "(?i)\\b(?:(Current|Reached|Highest|Your)\\s+)?(?:Safari\\s+)?(?:Milestone|Level|Tier)"
                    + "(?:\\s+Level)?\\s*[:#\\-]?\\s*" + LEVEL + "\\b");
    private static final Pattern NEGATIVE_STATUS = Pattern.compile(
            "(?i)\\b(?:LOCKED|NOT UNLOCKED|NOT REACHED|INCOMPLETE)\\b|\\bRequires?\\b");

    private SafariMilestoneParser() {
    }

    public static Levels parse(Iterable<String> receivedTexts) {
        Levels result = Levels.EMPTY;
        for (String raw : receivedTexts) result = result.merge(parseOne(raw));
        return result;
    }

    static Levels parseOne(String raw) {
        String text = HuntingTextParser.plain(raw);
        if (text.isBlank()) return Levels.EMPTY;
        boolean negative = NEGATIVE_STATUS.matcher(text).find();
        String contextBiome = "";
        Levels result = Levels.EMPTY;
        for (String rawLine : text.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            Matcher biomeMatcher = BIOME_NAME.matcher(line);
            if (biomeMatcher.find()) contextBiome = titleCase(biomeMatcher.group(1));

            Matcher combined = BIOME_LEVEL.matcher(line);
            if (combined.find()) {
                if (!negative) result = result.with(titleCase(combined.group(1)), level(combined.group(2)));
                continue;
            }
            Matcher contextual = CONTEXT_LEVEL.matcher(line);
            if (contextual.find() && !contextBiome.isBlank()) {
                boolean explicitlyCurrent = contextual.group(1) != null;
                if (!negative || explicitlyCurrent) {
                    result = result.with(contextBiome, level(contextual.group(2)));
                }
            }
        }
        return result;
    }

    private static int level(String raw) {
        try {
            return Math.clamp(Integer.parseInt(raw), 0, 10);
        } catch (NumberFormatException ignored) {
            return switch (raw.toUpperCase(Locale.ROOT)) {
                case "I" -> 1; case "II" -> 2; case "III" -> 3; case "IV" -> 4; case "V" -> 5;
                case "VI" -> 6; case "VII" -> 7; case "VIII" -> 8; case "IX" -> 9; case "X" -> 10;
                default -> 0;
            };
        }
    }

    private static String titleCase(String value) {
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT);
    }

    public record Levels(int cavern, int forest, int haunted, int icy) {
        public static final Levels EMPTY = new Levels(0, 0, 0, 0);

        public Levels {
            cavern = Math.clamp(cavern, 0, 10);
            forest = Math.clamp(forest, 0, 10);
            haunted = Math.clamp(haunted, 0, 10);
            icy = Math.clamp(icy, 0, 10);
        }

        public Levels merge(Levels other) {
            return new Levels(Math.max(cavern, other.cavern), Math.max(forest, other.forest),
                    Math.max(haunted, other.haunted), Math.max(icy, other.icy));
        }

        Levels with(String biome, int level) {
            return switch (biome) {
                case "Cavern" -> new Levels(Math.max(cavern, level), forest, haunted, icy);
                case "Forest" -> new Levels(cavern, Math.max(forest, level), haunted, icy);
                case "Haunted" -> new Levels(cavern, forest, Math.max(haunted, level), icy);
                case "Icy" -> new Levels(cavern, forest, haunted, Math.max(icy, level));
                default -> this;
            };
        }

        public boolean empty() {
            return cavern == 0 && forest == 0 && haunted == 0 && icy == 0;
        }
    }
}
