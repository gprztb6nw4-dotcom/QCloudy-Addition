package cloudy.autume.addition.inventory;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Optional, dependency-free bridge to Bazaar data already cached by another
 * client mod. QCloudy never performs a price HTTP request of its own.
 */
public final class ShardPriceService {
    private static final String SKYBLOCKER_CLASS = "de.hysky.skyblocker.utils.ItemUtils";
    private static final ShardPriceService INSTANCE = new ShardPriceService();

    private volatile Bridge bridge;

    private ShardPriceService() {
    }

    public static ShardPriceService instance() {
        return INSTANCE;
    }

    public Availability availability() {
        return bridge().availability();
    }

    public String sourceName() {
        return bridge().sourceName();
    }

    public Map<String, Double> snapshot(boolean instantBuy) {
        Bridge current = bridge();
        if (!current.availability().available()) return Map.of();
        Map<String, Double> result = new LinkedHashMap<>();
        for (ShardFusionCatalog.Shard shard : ShardFusionCatalog.instance().shards()) {
            double price = current.price(shard.bazaarId(), instantBuy);
            if (Double.isFinite(price) && price > 0.0) result.put(shard.id(), price);
        }
        return Map.copyOf(result);
    }

    /** Re-probes after a resource reload or optional-mod update. */
    public void reset() {
        bridge = null;
    }

    private Bridge bridge() {
        Bridge current = bridge;
        if (current != null) return current;
        synchronized (this) {
            current = bridge;
            if (current == null) {
                current = detect();
                bridge = current;
            }
            return current;
        }
    }

    private static Bridge detect() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("skyblocker")) {
            try {
                Class<?> type = Class.forName(SKYBLOCKER_CLASS, false,
                        Thread.currentThread().getContextClassLoader());
                Method method = type.getMethod("getItemPrice", String.class, boolean.class);
                return new SkyblockerBridge(method);
            } catch (ReflectiveOperationException | LinkageError exception) {
                return new UnavailableBridge(Availability.INCOMPATIBLE,
                        "Skyblocker (incompatible price API)");
            }
        }
        if (loader.isModLoaded("skyhanni") || loader.isModLoaded("firmament")) {
            // These mods currently keep their Bazaar structures internal. Using
            // private implementation fields would silently break across updates.
            return new UnavailableBridge(Availability.NO_STABLE_API,
                    "SkyHanni/Firmament (no stable public price API)");
        }
        return new UnavailableBridge(Availability.NO_PROVIDER, "None");
    }

    public enum Availability {
        AVAILABLE(true), NO_PROVIDER(false), NO_STABLE_API(false), INCOMPATIBLE(false), NO_DATA(false);

        private final boolean available;

        Availability(boolean available) {
            this.available = available;
        }

        public boolean available() {
            return available;
        }
    }

    private interface Bridge {
        Availability availability();

        String sourceName();

        double price(String bazaarId, boolean instantBuy);
    }

    private record UnavailableBridge(Availability availability, String sourceName) implements Bridge {
        @Override
        public double price(String bazaarId, boolean instantBuy) {
            return Double.NaN;
        }
    }

    private static final class SkyblockerBridge implements Bridge {
        private final Method method;

        private SkyblockerBridge(Method method) {
            this.method = method;
        }

        @Override
        public Availability availability() {
            return Availability.AVAILABLE;
        }

        @Override
        public String sourceName() {
            return "Skyblocker client cache";
        }

        @Override
        public double price(String bazaarId, boolean instantBuy) {
            try {
                Object value = method.invoke(null, bazaarId, instantBuy);
                if (value instanceof OptionalDouble optional) return optional.orElse(Double.NaN);
                if (value instanceof Optional<?> optional && optional.orElse(null) instanceof Number number) {
                    return number.doubleValue();
                }
                if (value instanceof Number number) return number.doubleValue();
            } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
                // A single malformed/missing price must not disable Ironman mode.
            }
            return Double.NaN;
        }
    }
}
