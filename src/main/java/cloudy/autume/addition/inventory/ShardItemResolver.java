package cloudy.autume.addition.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Resolves Shard icons from ItemStacks the client has already received.
 * Player resource packs therefore remain authoritative for rendering.
 */
final class ShardItemResolver {
    private static final long REFRESH_INTERVAL_MILLIS = 1_000L;
    private static final String MODEL_ROOT = "qcloudy_addition:shards/";
    /**
     * Appearance-bearing stacks are safe to share between guide screens. This
     * also lets a Shard seen in a server menu continue to use the resource
     * pack's native rendering after that menu has closed.
     */
    private static final Map<String, ItemStack> OBSERVED = new ConcurrentHashMap<>();
    private static final Map<String, ItemStack> BUNDLED = new ConcurrentHashMap<>();
    private static final AtomicLong LAST_REFRESH = new AtomicLong();
    private final ShardFusionCatalog catalog;

    ShardItemResolver(ShardFusionCatalog catalog) {
        this.catalog = catalog;
    }

    ItemStack item(ShardFusionCatalog.Shard shard) {
        refresh(Minecraft.getInstance());
        return cachedOrBundled(shard);
    }

    ItemStack cachedOrBundled(ShardFusionCatalog.Shard shard) {
        ItemStack item = OBSERVED.get(shard.id());
        if (item != null) return item;
        return BUNDLED.computeIfAbsent(shard.id(), ignored -> bundledIcon(shard));
    }

    /** Builds the complete offline fallback without contacting a texture service. */
    static ItemStack bundledIcon(ShardFusionCatalog.Shard shard) {
        ItemStack icon = new ItemStack(Items.PLAYER_HEAD);
        icon.set(DataComponents.ITEM_MODEL, bundledModel(shard));
        return icon;
    }

    static Identifier bundledModel(ShardFusionCatalog.Shard shard) {
        return Identifier.parse(MODEL_ROOT + shard.id().toLowerCase(Locale.ROOT));
    }

    void refresh(Minecraft minecraft) {
        long now = System.currentTimeMillis();
        if (minecraft.player == null) return;
        long previous = LAST_REFRESH.get();
        if (now - previous < REFRESH_INTERVAL_MILLIS
                || !LAST_REFRESH.compareAndSet(previous, now)) return;
        for (var slot : minecraft.player.containerMenu.slots) {
            remember(slot.getItem());
        }
    }

    void remember(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        ShardFusionCatalog.Shard shard = catalog.byItemId(SkyBlockItemData.itemId(stack)).orElse(null);
        if (shard == null) {
            String name = stack.getHoverName().getString().trim();
            if (name.toLowerCase(Locale.ROOT).endsWith(" shard")) {
                shard = catalog.byName(name).orElse(null);
            }
        }
        if (shard == null) return;
        ItemStack icon = stack.copy();
        icon.setCount(1);
        OBSERVED.put(shard.id(), icon);
    }

    /** Keeps unit tests isolated without changing normal client-session reuse. */
    static void resetSessionCache() {
        OBSERVED.clear();
        BUNDLED.clear();
        LAST_REFRESH.set(0L);
    }
}
