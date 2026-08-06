package cloudy.autume.addition.inventory.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class StoragePageKeyTest {
    @Test
    void parsesOverviewAndPageTitlesWithoutOffByOneErrors() {
        assertEquals(new StoragePageKey(0), StoragePageKey.fromOverviewSlot(9));
        assertEquals(new StoragePageKey(8), StoragePageKey.fromOverviewSlot(17));
        assertEquals(new StoragePageKey(9), StoragePageKey.fromOverviewSlot(27));
        assertEquals(new StoragePageKey(26), StoragePageKey.fromOverviewSlot(44));
        assertNull(StoragePageKey.fromOverviewSlot(18));
        assertEquals(new StoragePageKey(0), StoragePageKey.fromTitle("Ender Chest (1/9)"));
        assertEquals(new StoragePageKey(14), StoragePageKey.fromTitle("Large Backpack (Slot #6)"));
    }
}
