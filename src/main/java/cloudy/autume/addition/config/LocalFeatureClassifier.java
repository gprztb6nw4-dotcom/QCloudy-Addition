package cloudy.autume.addition.config;

import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Small, deterministic metadata classifier used only after native provider
 * categories and verified path rules fail to classify an external feature.
 *
 * <p>The model is a fixed weighted bag-of-words classifier. It does not train,
 * access the network, load a large model, or inspect gameplay/server data. A
 * low-confidence result is deliberately rejected so QCA never silently merges
 * an uncertain feature into an unrelated category.</p>
 */
final class LocalFeatureClassifier {
    private static final int MINIMUM_SCORE = 5;
    private static final int MINIMUM_MARGIN = 2;
    private static final Map<ConfigScreen.Category, Map<String, Integer>> WEIGHTS = weights();

    private LocalFeatureClassifier() { }

    static @Nullable Result classify(String path, String title, String description) {
        String text = normalize(path + " " + title + " " + description);
        EnumMap<ConfigScreen.Category, Integer> scores = new EnumMap<>(ConfigScreen.Category.class);
        for (Map.Entry<ConfigScreen.Category, Map<String, Integer>> category : WEIGHTS.entrySet()) {
            int score = 0;
            for (Map.Entry<String, Integer> signal : category.getValue().entrySet()) {
                if (containsToken(text, signal.getKey())) score += signal.getValue();
            }
            scores.put(category.getKey(), score);
        }

        ConfigScreen.Category bestCategory = ConfigScreen.Category.GENERAL;
        int best = Integer.MIN_VALUE;
        int second = Integer.MIN_VALUE;
        int positiveTotal = 0;
        for (Map.Entry<ConfigScreen.Category, Integer> entry : scores.entrySet()) {
            int score = entry.getValue();
            positiveTotal += Math.max(0, score);
            if (score > best) {
                second = best;
                best = score;
                bestCategory = entry.getKey();
            } else if (score > second) {
                second = score;
            }
        }
        if (best < MINIMUM_SCORE || best - Math.max(0, second) < MINIMUM_MARGIN) return null;
        double confidence = positiveTotal == 0 ? 0.0 : best / (double) positiveTotal;
        return new Result(bestCategory, Math.clamp(confidence, 0.0, 1.0), best - Math.max(0, second));
    }

    private static boolean containsToken(String text, String signal) {
        if (signal.indexOf(' ') >= 0) return text.contains(signal);
        return (" " + text + " ").contains(" " + signal + " ");
    }

    private static String normalize(String value) {
        return value.replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .replace('_', ' ').replace('-', ' ')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]+", " ")
                .replaceAll("\\s+", " ").strip();
    }

    private static Map<ConfigScreen.Category, Map<String, Integer>> weights() {
        EnumMap<ConfigScreen.Category, Map<String, Integer>> result =
                new EnumMap<>(ConfigScreen.Category.class);
        put(result, ConfigScreen.Category.MAPS, "waypoint", 6, "map", 5, "coordinate", 4,
                "fairy", 2, "beacon", 2);
        put(result, ConfigScreen.Category.ITEMS_AND_MENUS, "inventory", 6, "tooltip", 6,
                "item", 4, "menu", 4, "pet", 4, "bazaar", 3, "auction", 3, "storage", 3);
        put(result, ConfigScreen.Category.COMBAT, "combat", 6, "kuudra", 6, "crimson", 6,
                "dragon", 5, "damage", 4, "boss", 3, "mob", 2, "dojo", 3);
        put(result, ConfigScreen.Category.DUNGEONS, "dungeon", 7, "catacomb", 7,
                "terminal", 5, "secret", 4, "party finder", 5, "floor", 2);
        put(result, ConfigScreen.Category.SLAYER, "slayer", 8, "revenant", 5,
                "tarantula", 5, "sven", 5, "voidgloom", 5, "inferno", 5);
        put(result, ConfigScreen.Category.MINING, "mining", 7, "dwarven", 6,
                "glacite", 6, "commission", 5, "powder", 5, "hotm", 5, "gemstone", 3);
        put(result, ConfigScreen.Category.FARMING, "farming", 7, "garden", 6,
                "crop", 5, "visitor", 4, "pest", 4, "composter", 4);
        put(result, ConfigScreen.Category.FORAGING, "foraging", 7, "galatea", 6,
                "torrhus", 6, "sweep", 5, "tree", 3, "forest", 2);
        put(result, ConfigScreen.Category.FISHING, "fishing", 7, "bobber", 6,
                "sea creature", 6, "hook", 4, "fish", 3, "trophy", 3);
        put(result, ConfigScreen.Category.HUNTING, "hunting", 7, "safari", 6,
                "lasso", 6, "critter", 5, "shard", 4, "warden", 2);
        put(result, ConfigScreen.Category.RIFT, "rift", 8, "motes", 4, "vampire", 3);
        put(result, ConfigScreen.Category.EVENTS, "event", 6, "spooky", 5,
                "raffle", 5, "carnival", 5, "anniversary", 4, "mayor", 2);
        put(result, ConfigScreen.Category.GENERAL, "chat", 4, "notification", 3,
                "performance", 3, "interface", 3, "general", 2);
        return Map.copyOf(result);
    }

    private static void put(Map<ConfigScreen.Category, Map<String, Integer>> target,
                            ConfigScreen.Category category, Object... pairs) {
        Map<String, Integer> values = new LinkedHashMap<>();
        for (int index = 0; index + 1 < pairs.length; index += 2) {
            values.put((String) pairs[index], (Integer) pairs[index + 1]);
        }
        target.put(category, Map.copyOf(values));
    }

    record Result(ConfigScreen.Category category, double confidence, int margin) { }
}
