package cloudy.autume.addition.inventory.storage;

import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class StorageCacheManagerTest {
    @Test
    void invalidStackEncodingKeepsAnEmptySlotPlaceholder() {
        ListTag items = new ListTag();

        boolean stored = StorageCacheManager.appendStackSafely(items, ItemStack.EMPTY, (tag, stack) -> {
            throw new IllegalStateException("stale registry holder");
        });

        assertFalse(stored);
        assertEquals(1, items.size());
        assertTrue(items.getCompoundOrEmpty(0).isEmpty());
    }

    @Test
    void validStackEncodingKeepsTheEncodedSlot() {
        ListTag items = new ListTag();

        boolean stored = StorageCacheManager.appendStackSafely(items, ItemStack.EMPTY,
                (tag, stack) -> tag.putString("stack", "ok"));

        assertTrue(stored);
        assertEquals("ok", items.getCompoundOrEmpty(0).getStringOr("stack", ""));
    }
}
