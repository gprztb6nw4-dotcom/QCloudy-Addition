package cloudy.autume.addition.hud;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.config.ModConfig;
import cloudy.autume.addition.hunting.HuntingTextParser;
import cloudy.autume.addition.hunting.HuntingTracker;
import cloudy.autume.addition.i18n.ModText;
import cloudy.autume.addition.tracker.IslandArea;
import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/** One responsive panel shared by Torrhus Canyon and Critter Safari features. */
public final class HuntingHudRenderer {
    public static final int WIDTH = 296;
    private static final int PADDING = 7;
    private static final int LINE_HEIGHT = 10;

    private HuntingHudRenderer() {
    }

    public static void render(GuiGraphicsExtractor graphics) {
        ModConfig.PanelStyle style = ConfigManager.get().hudStyle.hunting;
        List<Line> lines = lines();
        int height = height(lines, style);
        HudPanel.background(graphics, 0, 0, WIDTH, height, style);
        int y = 5;
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            Line line = lines.get(lineIndex);
            List<FormattedCharSequence> wrapped = wrap(line, style);
            for (FormattedCharSequence part : wrapped) {
                graphics.text(Minecraft.getInstance().font, part, PADDING + line.indent, y,
                        lineIndex == 0 ? 0xFF000000 | style.titleColor : line.color, style.textShadow);
                y += LINE_HEIGHT;
            }
            if (line.section) y += 2;
        }
    }

    public static int currentHeight() {
        return height(lines(), ConfigManager.get().hudStyle.hunting);
    }

    public static boolean loaded() {
        var config = ConfigManager.get().hunting;
        if (LocationTracker.area() == IslandArea.TORRHUS_CANYON) {
            return config.torrhusTracker || config.miriaContest || config.critterBehavior
                    || config.benefactorHud || config.treeCritterTimer && HuntingTracker.treeCritterTimer() != null;
        }
        if (LocationTracker.area() == IslandArea.CRITTER_SAFARI) {
            return config.safariDashboard || config.safariCritterdex || config.floorDropAssistant
                    || config.questItemTracker || config.wumpaHud;
        }
        return false;
    }

    private static List<Line> lines() {
        return LocationTracker.area() == IslandArea.CRITTER_SAFARI ? safariLines() : torrhusLines();
    }

    private static List<Line> torrhusLines() {
        var config = ConfigManager.get().hunting;
        List<Line> result = new ArrayList<>();
        result.add(Line.title("Torrhus Canyon"));
        if (config.torrhusTracker) {
            result.add(Line.section(ModText.get("hud.hunting.chapter"), 0xFFFFD45A));
            var chapter = HuntingTracker.chapter();
            if (config.showChapter) result.add(Line.value(ModText.get("hud.hunting.chapter_value"), value(chapter.chapter())));
            if (config.showCurrentTask) result.add(Line.value(ModText.get("hud.hunting.task"), value(chapter.task())));
            if (config.showTaskProgress) result.add(Line.value(ModText.get("hud.hunting.progress"), value(chapter.progress())));
            if (config.showCompletedTasks) result.add(Line.value(ModText.get("hud.hunting.completed"), value(chapter.completed())));
            if (config.showChapterTotalProgress) result.add(Line.value(ModText.get("hud.hunting.total_progress"), value(chapter.totalProgress())));
            if (config.showNextUnlock) result.add(Line.value(ModText.get("hud.hunting.next_unlock"), value(chapter.nextUnlock())));
            appendTorrhusResources(result, config);
        }
        if (config.treeCritterTimer && HuntingTracker.treeCritterTimer() != null) {
            result.add(Line.section("Tree Protection Order", 0xFFFF6B6B));
            result.add(Line.value("Critter in", HuntingTracker.treeCritterTimer().display()));
        }
        if (config.miriaContest) appendContest(result, config);
        if (config.critterBehavior && !HuntingTracker.behaviorName().isBlank()) {
            result.add(Line.section(ModText.get("hud.hunting.behavior"), 0xFFFFB866));
            result.add(Line.value(HuntingTracker.behaviorName(), value(HuntingTracker.behaviorStatus())));
        }
        if (config.benefactorHud) appendBenefactor(result, config);
        return result;
    }

    private static void appendTorrhusResources(List<Line> lines, ModConfig.Hunting config) {
        lines.add(Line.section(ModText.get("hud.hunting.resources"), 0xFFFFD45A));
        if (config.showForestWhispers) appendResource(lines, "Forest Whispers", HuntingTextParser.Resource.FOREST_WHISPERS, 0xFF55E3C0);
        if (config.showDesertWhispers) appendResource(lines, "Desert Whispers", HuntingTextParser.Resource.DESERT_WHISPERS, 0xFFFFC55C);
        if (config.showForestEssence) appendResource(lines, "Forest Essence", HuntingTextParser.Resource.FOREST_ESSENCE, 0xFF67E87A);
        if (config.showSafariEssenceTorrhus) appendResource(lines, "Safari Essence", HuntingTextParser.Resource.SAFARI_ESSENCE, 0xFFFFB35C);
        if (config.showSweep) appendResource(lines, "Sweep", HuntingTextParser.Resource.SWEEP, 0xFF8FE3FF);
        if (config.showForestFortune) appendResource(lines, "Forest Fortune", HuntingTextParser.Resource.FOREST_FORTUNE, 0xFFB9F78A);
    }

    private static void appendResource(List<Line> lines, String name, HuntingTextParser.Resource resource, int color) {
        double amount = HuntingTracker.resource(resource);
        lines.add(new Line(name + ": " + (amount < 0 ? "—" : CompactNumbers.format(amount)), color, 3, false));
    }

    private static void appendContest(List<Line> lines, ModConfig.Hunting config) {
        var contest = HuntingTracker.contest();
        lines.add(Line.section("Miria's Contest", 0xFFFF72DB));
        if (config.contestNextBracket) {
            String value = contest.nextScore() < 0 ? "—"
                    : (contest.nextBracket().isBlank() ? "" : contest.nextBracket() + " · ")
                    + CompactNumbers.format(contest.nextScore());
            lines.add(Line.value(ModText.get("hud.hunting.next_bracket"), value));
        }
        if (config.contestExpectedTicket) lines.add(Line.value(ModText.get("hud.hunting.expected_ticket"), value(contest.ticket())));
        if (config.contestRemainingScore) {
            String value = contest.remaining() < 0 ? "—" : CompactNumbers.format(contest.remaining());
            lines.add(Line.value(ModText.get("hud.hunting.score_remaining"), value));
        }
    }

    private static void appendBenefactor(List<Line> lines, ModConfig.Hunting config) {
        var benefactor = HuntingTracker.benefactor();
        lines.add(Line.section("Benefactor", 0xFFA98CFF));
        if (config.benefactorStatus) lines.add(Line.value(ModText.get("hud.hunting.status"),
                benefactor == HuntingTracker.BenefactorState.EMPTY ? "—"
                        : benefactor.active() ? ModText.get("hud.hunting.active") : ModText.get("hud.hunting.inactive")));
        if (config.benefactorTimer) {
            long seconds = benefactor.remainingSeconds();
            lines.add(Line.value(ModText.get("hud.hunting.time_left"), seconds < 0 ? "—" : HuntingTracker.durationText(seconds)));
        }
        if (config.benefactorEffects && !benefactor.effect().isBlank()) lines.add(Line.value(ModText.get("hud.hunting.effect"), benefactor.effect()));
        if (config.benefactorDonation && !benefactor.donation().isBlank()) lines.add(Line.value(ModText.get("hud.hunting.donation"), benefactor.donation()));
    }

    private static List<Line> safariLines() {
        var config = ConfigManager.get().hunting;
        List<Line> result = new ArrayList<>();
        result.add(Line.title("Critter Safari"));
        if (config.safariDashboard) {
            result.add(Line.section(ModText.get("hud.hunting.run_dashboard"), 0xFFFFD45A));
            if (config.safariShards) {
                result.add(Line.value(ModText.get("hud.hunting.shards"), Integer.toString(HuntingTracker.safariShardCount())));
                String breakdown = joinedCounts(HuntingTracker.shardDrops());
                if (!breakdown.isBlank()) result.add(new Line(breakdown, 0xFFC7D3DA, 6, false));
            }
            if (config.safariRunTime) result.add(Line.value(ModText.get("hud.hunting.run_time"),
                    HuntingTracker.durationText(HuntingTracker.safariRunMillis() / 1_000)));
            if (config.safariTicketTier) result.add(Line.value(ModText.get("hud.hunting.ticket_tier"), value(HuntingTracker.safariTicketTier())));
        }
        if (config.safariCritterdex) appendCritterdex(result, config);
        if (config.floorDropAssistant || config.questItemTracker) appendSafariItems(result, config);
        if (config.wumpaHud) {
            result.add(Line.section("Wumpa Encounter", 0xFF8FD8FF));
            if (HuntingTracker.wumpaSpawned()) {
                result.add(Line.value("Wumpa", ModText.get("hud.hunting.wumpa_spawned")));
            } else if (config.wumpaRequirements) {
                appendWumpaRequirements(result);
            }
            if (config.wumpaPhase) result.add(Line.value(ModText.get("hud.hunting.phase"), HuntingTracker.wumpaPhase().display));
        }
        return result;
    }

    private static void appendWumpaRequirements(List<Line> lines) {
        Set<String> captured = HuntingTracker.wumpaPrerequisiteCaptures();
        long count = HuntingTextParser.WUMPA_PREREQUISITES.stream().filter(captured::contains).count();
        lines.add(Line.value(ModText.get("hud.hunting.icy_requirements"),
                count + "/" + HuntingTextParser.WUMPA_PREREQUISITES.size()));
        for (String critter : HuntingTextParser.WUMPA_PREREQUISITES) {
            boolean complete = captured.contains(critter);
            lines.add(new Line((complete ? "✔ " : "✘ ") + critter,
                    complete ? 0xFF55E875 : 0xFFFF6B6B, 6, false));
        }
    }

    private static void appendCritterdex(List<Line> lines, ModConfig.Hunting config) {
        Set<String> captured = HuntingTracker.capturedCritters();
        lines.add(Line.section("Safari Run Critterdex", 0xFF9FF5B2));
        if (config.critterdexBiomeProgress) {
            for (Map.Entry<String, List<String>> entry : HuntingTextParser.SAFARI_CRITTERS.entrySet()) {
                long count = entry.getValue().stream().filter(captured::contains).count();
                lines.add(new Line(entry.getKey() + ": " + count + "/" + entry.getValue().size(), 0xFFD8E4EB, 3, false));
            }
        }
        String biome = HuntingTracker.safariBiome();
        List<String> critters = HuntingTextParser.SAFARI_CRITTERS.get(biome);
        if (critters == null) return;
        if (config.critterdexCapturedNames) {
            StringJoiner names = new StringJoiner(", ");
            critters.stream().filter(captured::contains).forEach(names::add);
            lines.add(Line.value(ModText.get("hud.hunting.captured"), names.length() == 0 ? "—" : names.toString()));
        }
        if (config.critterdexMissingNames) {
            StringJoiner names = new StringJoiner(", ");
            critters.stream().filter(name -> !captured.contains(name)).forEach(names::add);
            lines.add(Line.value(ModText.get("hud.hunting.missing"), names.length() == 0 ? ModText.get("hud.hunting.none") : names.toString()));
        }
    }

    private static void appendSafariItems(List<Line> lines, ModConfig.Hunting config) {
        lines.add(Line.section(ModText.get("hud.hunting.safari_items"), 0xFFFFC96B));
        if (config.floorDropAssistant && config.floorDropDistance) {
            double distance = HuntingTracker.nearestFloorDrop();
            lines.add(Line.value(ModText.get("hud.hunting.nearest_floor_drop"),
                    distance < 0 ? "—" : String.format(Locale.ROOT, "%.1fm", distance)));
        }
        if (config.questItemTracker) {
            String values = joinedCounts(HuntingTracker.questItems());
            lines.add(Line.value(ModText.get("hud.hunting.quest_items"), values.isBlank() ? "—" : values));
        }
    }

    private static String joinedCounts(Map<String, Integer> values) {
        StringJoiner result = new StringJoiner(", ");
        values.forEach((name, count) -> result.add(name + (count > 1 ? " x" + count : "")));
        return result.toString();
    }

    private static String value(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private static int height(List<Line> lines, ModConfig.PanelStyle style) {
        int height = 10;
        for (Line line : lines) height += wrap(line, style).size() * LINE_HEIGHT + (line.section ? 2 : 0);
        return Math.max(28, height);
    }

    private static List<FormattedCharSequence> wrap(Line line, ModConfig.PanelStyle style) {
        int available = WIDTH - PADDING * 2 - line.indent;
        List<FormattedCharSequence> wrapped = Minecraft.getInstance().font.split(
                HudPanel.styledText(line.text, style), available);
        return wrapped.isEmpty() ? List.of(Component.literal(" ").getVisualOrderText()) : wrapped;
    }

    private record Line(String text, int color, int indent, boolean section) {
        static Line title(String value) { return new Line(value, 0xFF7FDBFF, 0, true); }
        static Line section(String value, int color) { return new Line(value, color, 0, true); }
        static Line value(String label, String value) { return new Line(label + ": " + value, 0xFFD8E4EB, 3, false); }
    }
}
