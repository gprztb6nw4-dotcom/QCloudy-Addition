package cloudy.autume.addition.hunting;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bounded parser for received Tree Gift chat. The personal reward summary is
 * the ownership proof: unlike the public header and creature lines, Hypixel
 * sends that summary only to the player who earned the gift.
 */
final class TreeGiftAlertSession {
    private static final long SESSION_TIMEOUT_MS = 15_000L;
    /**
     * Hypixel can send a spawned-creature line immediately after the closing
     * Tree Gift border. Keep only the proven personal ownership for this short
     * hand-off window instead of accepting arbitrary nearby public messages.
     */
    private static final long POST_GIFT_CREATURE_WINDOW_MS = 5_000L;

    private final Set<String> pending = new LinkedHashSet<>();
    private final Set<String> emitted = new LinkedHashSet<>();
    private boolean open;
    private boolean personalSummary;
    private boolean bonusSection;
    private long lastMessageAt;
    private long recentPersonalUntil;

    List<String> accept(Component message, Map<String, Boolean> enabled, long now) {
        if (message == null || enabled == null) return List.of();
        if (open && now - lastMessageAt > SESSION_TIMEOUT_MS) reset();
        expireRecentOwnership(now);

        String[] lines = message.getString().split("\\R", -1);
        boolean containsBorder = false;
        boolean containsPersonalSummary = false;
        for (String rawLine : lines) {
            String line = HuntingTextParser.plain(rawLine);
            containsBorder |= HuntingTextParser.treeGiftBorder(line);
            containsPersonalSummary |= HuntingTextParser.personalTreeGiftRewardSummary(line);
        }
        // Some chat formatters preserve one received multi-line component but
        // omit its decorative borders. A summary in that exact component is
        // still personal proof for a creature row earlier in the same value.
        if (!open && !containsBorder && containsPersonalSummary) {
            recentPersonalUntil = now + POST_GIFT_CREATURE_WINDOW_MS;
        }

        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String rawLine : lines) {
            result.addAll(acceptLine(message, HuntingTextParser.plain(rawLine), enabled, now));
        }
        return List.copyOf(result);
    }

    private List<String> acceptLine(
            Component message,
            String line,
            Map<String, Boolean> enabled,
            long now
    ) {
        if (line.isBlank()) return List.of();
        if (HuntingTextParser.treeGiftBorder(line)) {
            if (open) close(now);
            else begin(now);
            return List.of();
        }

        if (!open) {
            if (HuntingTextParser.personalTreeGiftRewardSummary(line)) {
                recentPersonalUntil = now + POST_GIFT_CREATURE_WINDOW_MS;
                return emit(HuntingTextParser.treeGiftHoverLoot(message, enabled));
            }
            if (recentPersonalUntil >= now) {
                String creature = HuntingTextParser.treeGiftWildCreatureLoot(line, enabled);
                if (!creature.isBlank()) return emit(List.of(creature));
            }
            return List.of();
        }

        lastMessageAt = now;
        if (HuntingTextParser.treeGiftBonusHeader(line)) bonusSection = true;

        LinkedHashSet<String> found = new LinkedHashSet<>();
        if (HuntingTextParser.personalTreeGiftRewardSummary(line)) {
            personalSummary = true;
            recentPersonalUntil = now + POST_GIFT_CREATURE_WINDOW_MS;
            found.addAll(HuntingTextParser.treeGiftHoverLoot(message, enabled));
        }

        String chatLoot = HuntingTextParser.treeGiftChatLoot(line, enabled, bonusSection);
        if (!chatLoot.isBlank()) {
            if (personalBlock()) found.add(chatLoot);
            else pending.add(chatLoot);
        }

        // Do this after every line rather than only on the summary line. It
        // supports both observed message orders without weakening ownership.
        if (personalBlock()) {
            found.addAll(pending);
            pending.clear();
        }
        return emit(found);
    }

    void reset() {
        clearBlock();
        recentPersonalUntil = 0L;
        emitted.clear();
    }

    private void clearBlock() {
        open = false;
        personalSummary = false;
        bonusSection = false;
        lastMessageAt = 0L;
        pending.clear();
    }

    private void begin(long now) {
        reset();
        open = true;
        lastMessageAt = now;
    }

    private void close(long now) {
        boolean personal = personalBlock();
        clearBlock();
        if (personal) {
            recentPersonalUntil = now + POST_GIFT_CREATURE_WINDOW_MS;
        } else {
            recentPersonalUntil = 0L;
            emitted.clear();
        }
    }

    private void expireRecentOwnership(long now) {
        if (!open && recentPersonalUntil > 0L && now > recentPersonalUntil) {
            recentPersonalUntil = 0L;
            emitted.clear();
        }
    }

    private List<String> emit(Iterable<String> found) {
        List<String> result = new ArrayList<>();
        for (String loot : found) {
            if (!loot.isBlank() && emitted.add(loot)) result.add(loot);
        }
        return List.copyOf(result);
    }

    private boolean personalBlock() {
        // The summary is the only non-public line and therefore the ownership
        // boundary. Requiring the contribution sentence as well caused valid
        // gifts to be lost whenever Hypixel changed that display sentence.
        return personalSummary;
    }
}
