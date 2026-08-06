package cloudy.autume.addition.tracker;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PetTracker {
    private static final Pattern TAB_NAME = Pattern.compile("^\\[Lvl ([\\d,]+)] (?:\\[\\d+✦] )?([\\w -]+?)(?: ✦)?$");
    private static final Pattern TAB_XP = Pattern.compile("^(?:(MAX LEVEL)|(?:\\+)?([\\d,.kKmMbBtT]+)(?:/([\\d,.kKmMbBtT]+))? XP(?: \\(([\\d.]+)%\\))?)$");
    private static final Pattern TAB_OVERFLOW_XP = Pattern.compile("^\\+([\\d,.kKmMbBtT]+) XP$");
    private static final Pattern AUTOPET = Pattern.compile("^Autopet equipped your \\[Lvl (\\d+)] (?:\\[\\d+✦] )?([\\w -]+?)(?: ✦)?! VIEW RULE(?: \\(\\d+\\))?$");
    private static final Pattern SUMMON = Pattern.compile("^You summoned your (.+?)(?: ✦)?!$");
    private static final Pattern DESPAWN = Pattern.compile("^You despawned your (?:.+?|pet)!$", Pattern.CASE_INSENSITIVE);

    private static PetSnapshot current;

    private PetTracker() {
    }

    public static void onChat(String raw, boolean overlay) {
        if (overlay || !LocationTracker.isSkyBlock()) return;
        updateFromChat(raw.trim());
    }

    static void updateFromChat(String message) {
        Matcher autoPet = AUTOPET.matcher(message);
        if (autoPet.matches()) {
            String name = autoPet.group(2);
            current = new PetSnapshot(name, autoPet.group(1), "", "", "", false, rememberedColor(name));
            PetSkinTracker.noteSkinMarker(name, message.contains("✦"));
            return;
        }
        Matcher summon = SUMMON.matcher(message);
        if (summon.matches()) {
            String name = summon.group(1);
            String level = current != null && current.name().equals(name) ? current.level() : "?";
            current = new PetSnapshot(name, level, "", "", "", false, rememberedColor(name));
            PetSkinTracker.noteSkinMarker(name, message.contains("✦"));
            return;
        }
        if (DESPAWN.matcher(message).matches()) current = null;
    }

    public static void updateFromTab(List<String> lines) {
        updateFromTab(lines, List.of());
    }

    public static void updateFromTab(List<String> lines, List<Component> components) {
        for (int index = 0; index < lines.size(); index++) {
            if (!lines.get(index).trim().equalsIgnoreCase("Pet:")) continue;
            if (index + 1 >= lines.size()) return;
            String petLine = lines.get(index + 1).trim();
            if (petLine.equalsIgnoreCase("No pet selected")) {
                current = null;
                return;
            }
            Matcher name = TAB_NAME.matcher(petLine);
            if (!name.matches()) return;

            String petName = name.group(2);
            PetSkinTracker.noteSkinMarker(petName, petLine.contains("✦"));
            int rarityColor = index + 1 < components.size()
                    ? findNameColor(components.get(index + 1), petName, rememberedColor(petName))
                    : rememberedColor(petName);

            String xpLine = index + 2 < lines.size() ? lines.get(index + 2).trim() : "";
            Matcher xp = TAB_XP.matcher(xpLine);
            if (xp.matches()) {
                boolean max = xp.group(1) != null;
                String overflow = "";
                for (int detail = index + 2; detail < Math.min(lines.size(), index + 6); detail++) {
                    Matcher overflowMatcher = TAB_OVERFLOW_XP.matcher(lines.get(detail).trim());
                    if (overflowMatcher.matches()) overflow = overflowMatcher.group(1);
                }
                current = new PetSnapshot(petName, name.group(1),
                        value(xp.group(2)), value(xp.group(3)), value(xp.group(4)), max, overflow, rarityColor);
            } else {
                current = new PetSnapshot(petName, name.group(1), "", "", "", false, rarityColor);
            }
            return;
        }
    }

    public static PetSnapshot current() {
        return current;
    }

    public static void reset() {
        current = null;
    }

    private static String value(String string) {
        return string == null ? "" : string;
    }

    private static int rememberedColor(String name) {
        return current != null && current.name().equalsIgnoreCase(name) ? current.rarityColor() : 0xFFFFFF;
    }

    private static int findNameColor(Component component, String petName, int fallback) {
        int result = fallback;
        if (component.getString().contains(petName) && component.getStyle().getColor() != null) {
            result = component.getStyle().getColor().getValue();
        }
        for (Component sibling : component.getSiblings()) {
            if (sibling.getString().contains(petName)) result = findNameColor(sibling, petName, result);
        }
        return result & 0xFFFFFF;
    }

    public record PetSnapshot(String name, String level, String currentXp, String nextXp,
                              String percentage, boolean maxLevel, String overflowXp, int rarityColor) {
        public PetSnapshot(String name, String level, String currentXp, String nextXp,
                           String percentage, boolean maxLevel, int rarityColor) {
            this(name, level, currentXp, nextXp, percentage, maxLevel, "", rarityColor);
        }
    }
}
