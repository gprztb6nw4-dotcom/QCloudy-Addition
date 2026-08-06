package cloudy.autume.addition.hunting;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bounded parser for one received Tree Gift chat block. Separate bonus rows are
 * eligible only after the same block proves that it contains this player's
 * contribution and personal reward summary.
 */
final class TreeGiftAlertSession {
    private static final long SESSION_TIMEOUT_MS = 15_000L;

    private final Set<String> pending = new LinkedHashSet<>();
    private final Set<String> emitted = new LinkedHashSet<>();
    private boolean open;
    private boolean header;
    private boolean contribution;
    private boolean personalSummary;
    private boolean bonusSection;
    private long lastMessageAt;

    List<String> accept(Component message, Map<String, Boolean> enabled, long now) {
        if (message == null || enabled == null) return List.of();
        if (open && now - lastMessageAt > SESSION_TIMEOUT_MS) reset();

        String line = HuntingTextParser.plain(message.getString());
        if (HuntingTextParser.treeGiftBorder(line)) {
            if (open) reset();
            else begin(now);
            return List.of();
        }

        // The personal summary is sufficient ownership evidence for its own
        // SHOW_TEXT even if another client mod hid the decorative block lines.
        if (!open) {
            return HuntingTextParser.personalTreeGiftLoot(message, enabled);
        }

        lastMessageAt = now;
        if (HuntingTextParser.treeGiftHeader(line)) header = true;
        if (HuntingTextParser.personalTreeGiftContribution(line)) contribution = true;
        if (HuntingTextParser.treeGiftBonusHeader(line)) bonusSection = true;

        LinkedHashSet<String> found = new LinkedHashSet<>();
        if (HuntingTextParser.personalTreeGiftRewardSummary(line)) {
            personalSummary = true;
            found.addAll(HuntingTextParser.personalTreeGiftLoot(message, enabled));
            if (personalBlock()) {
                found.addAll(pending);
                pending.clear();
            }
        }

        String chatLoot = HuntingTextParser.treeGiftChatLoot(line, enabled, bonusSection);
        if (!chatLoot.isBlank()) {
            if (personalBlock()) found.add(chatLoot);
            else if (header) pending.add(chatLoot);
        }

        List<String> result = new ArrayList<>();
        for (String loot : found) {
            if (emitted.add(loot)) result.add(loot);
        }
        return List.copyOf(result);
    }

    void reset() {
        open = false;
        header = false;
        contribution = false;
        personalSummary = false;
        bonusSection = false;
        lastMessageAt = 0L;
        pending.clear();
        emitted.clear();
    }

    private void begin(long now) {
        reset();
        open = true;
        lastMessageAt = now;
    }

    private boolean personalBlock() {
        return header && contribution && personalSummary;
    }
}
