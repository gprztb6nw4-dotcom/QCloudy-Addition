package cloudy.autume.addition.inventory;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.hunting.HuntingTracker;
import cloudy.autume.addition.hunting.SafariMilestoneParser;
import cloudy.autume.addition.i18n.ModText;
import cloudy.autume.addition.tracker.LocationTracker;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Adds only locally observed Safari milestone data to the received item tooltip. */
public final class SafariBeltTooltip {
    private static final Pattern BONUS = Pattern.compile(
            "(?i)\\b(Hunting Wisdom|Sweep|Hunting Fortune|Hunter Fortune|Cold Resistance)"
                    + "\\s*:?\\s*([+]?[0-9][0-9,.]*%?)");
    private SafariBeltTooltip() {
    }

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            var config = ConfigManager.get().hunting;
            if (!config.safariBeltTooltip || !LocationTracker.isSkyBlock()
                    || !"SAFARI_BELT".equals(SkyBlockItemData.itemId(stack))) return;

            SafariMilestoneParser.Levels observed = SafariMilestoneParser.parse(
                    lines.stream().map(Component::getString).toList());
            HuntingTracker.updateSafariMilestones(Minecraft.getInstance(), observed);
            var progress = HuntingTracker.currentProgress(Minecraft.getInstance());
            Map<String, String> bonuses = receivedBonuses(lines);
            lines.add(Component.empty());
            lines.add(Component.literal(ModText.get("tooltip.safari_belt.header"))
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            if (config.safariBeltMilestones) {
                lines.add(milestoneLine("Cavern", progress == null ? 0 : progress.safariBeltCavernLevel));
                lines.add(milestoneLine("Forest", progress == null ? 0 : progress.safariBeltForestLevel));
                lines.add(milestoneLine("Haunted", progress == null ? 0 : progress.safariBeltHauntedLevel));
                lines.add(milestoneLine("Icy", progress == null ? 0 : progress.safariBeltIcyLevel));
            }
            if (config.safariBeltBonuses) appendReceivedBonuses(lines, bonuses);
        });
    }

    private static Component milestoneLine(String biome, int level) {
        String value = level <= 0 ? "—" : roman(level);
        return Component.literal("  " + biome + " Milestone: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(value).withStyle(ChatFormatting.GREEN));
    }

    private static Map<String, String> receivedBonuses(List<Component> lines) {
        Map<String, String> bonuses = new LinkedHashMap<>();
        for (Component line : lines) {
            Matcher matcher = BONUS.matcher(line.getString());
            while (matcher.find()) {
                String name = matcher.group(1).equalsIgnoreCase("Hunter Fortune")
                        ? "Hunting Fortune" : titleCase(matcher.group(1));
                bonuses.put(name, matcher.group(2));
            }
        }
        return bonuses;
    }

    private static void appendReceivedBonuses(List<Component> lines, Map<String, String> bonuses) {
        if (bonuses.isEmpty()) {
            lines.add(Component.literal("  " + ModText.get("tooltip.safari_belt.bonuses_received"))
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        for (String name : List.of("Hunting Wisdom", "Sweep", "Hunting Fortune", "Cold Resistance")) {
            String value = bonuses.get(name);
            if (value == null) continue;
            lines.add(Component.literal("  " + name + ": ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(value).withStyle(ChatFormatting.AQUA)));
        }
    }

    private static String roman(int level) {
        return switch (Math.clamp(level, 1, 10)) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
            case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII"; case 9 -> "IX"; default -> "X";
        };
    }

    private static String titleCase(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(lower.length());
        boolean capitalize = true;
        for (char character : lower.toCharArray()) {
            result.append(capitalize ? Character.toUpperCase(character) : character);
            capitalize = character == ' ';
        }
        return result.toString();
    }
}
