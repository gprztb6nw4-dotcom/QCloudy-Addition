package cloudy.autume.addition.hud;

import java.util.Locale;

public final class CompactNumbers {
    private CompactNumbers() {
    }

    public static double parse(String raw) {
        if (raw == null) return 0.0;
        String value = raw.trim().replace(",", "").toLowerCase(Locale.ROOT);
        if (value.isEmpty() || value.equals("—")) return 0.0;
        double multiplier = 1.0;
        char suffix = value.charAt(value.length() - 1);
        if (suffix == 'k' || suffix == 'm' || suffix == 'b' || suffix == 't') {
            value = value.substring(0, value.length() - 1);
            multiplier = switch (suffix) {
                case 'k' -> 1_000.0;
                case 'm' -> 1_000_000.0;
                case 'b' -> 1_000_000_000.0;
                case 't' -> 1_000_000_000_000.0;
                default -> 1.0;
            };
        }
        try {
            return Double.parseDouble(value) * multiplier;
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    public static String format(String raw) {
        if (raw == null || raw.isBlank() || raw.equals("—")) return raw == null ? "" : raw;
        return format(parse(raw));
    }

    public static String format(double value) {
        double absolute = Math.abs(value);
        if (absolute >= 1_000_000_000_000.0) return oneDecimal(value / 1_000_000_000_000.0) + "t";
        if (absolute >= 1_000_000_000.0) return oneDecimal(value / 1_000_000_000.0) + "b";
        if (absolute >= 1_000_000.0) return oneDecimal(value / 1_000_000.0) + "m";
        if (absolute >= 1_000.0) return oneDecimal(value / 1_000.0) + "k";
        if (Math.rint(value) == value) return Long.toString(Math.round(value));
        return oneDecimal(value);
    }

    public static String percent(double value) {
        return oneDecimal(Math.clamp(value, 0.0, 100.0)) + "%";
    }

    private static String oneDecimal(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
