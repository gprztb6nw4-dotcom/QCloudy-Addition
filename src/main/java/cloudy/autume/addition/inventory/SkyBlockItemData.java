package cloudy.autume.addition.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

final class SkyBlockItemData {
    private static final ZoneId HYPIXEL_ZONE = ZoneId.of("America/New_York");
    private static final DateTimeFormatter LEGACY_TIMESTAMP = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral('/')
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral('/')
            .appendValueReduced(ChronoField.YEAR, 2, 2, 1950).appendLiteral(' ')
            .appendValue(ChronoField.CLOCK_HOUR_OF_AMPM, 1, 2, SignStyle.NEVER).appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(' ')
            .appendText(ChronoField.AMPM_OF_DAY)
            .toFormatter(Locale.US);

    private SkyBlockItemData() {
    }

    static CompoundTag attributes(ItemStack stack) {
        var custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null) return new CompoundTag();
        CompoundTag root = custom.copyTag();
        CompoundTag extra = root.getCompoundOrEmpty("ExtraAttributes");
        return extra.isEmpty() ? root : extra;
    }

    static String uuid(ItemStack stack) {
        return uuid(attributes(stack));
    }

    static String uuid(CompoundTag attributes) {
        String value = attributes.getStringOr("uuid", "").trim().toLowerCase(Locale.ROOT);
        return value.replace("-", "");
    }

    static String itemId(ItemStack stack) {
        return itemId(attributes(stack));
    }

    static String itemId(CompoundTag attributes) {
        return attributes.getStringOr("id", "").trim().toUpperCase(Locale.ROOT);
    }

    static boolean isHuntingBox(ItemStack stack) {
        return isHuntingBox(attributes(stack));
    }

    static boolean isHuntingBox(CompoundTag attributes) {
        return "HUNTING_TOOLKIT".equals(itemId(attributes)) || attributes.contains("tool_kit");
    }

    static Instant timestamp(ItemStack stack) {
        return timestamp(attributes(stack));
    }

    static Instant timestamp(CompoundTag attributes) {
        var numeric = attributes.getLong("timestamp");
        if (numeric.isPresent()) {
            try {
                return Instant.ofEpochMilli(numeric.get());
            } catch (RuntimeException ignored) {
                return null;
            }
        }
        String legacy = attributes.getStringOr("timestamp", "").trim();
        if (legacy.isEmpty()) return null;
        try {
            return LocalDateTime.parse(legacy, LEGACY_TIMESTAMP).atZone(HYPIXEL_ZONE).toInstant();
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
