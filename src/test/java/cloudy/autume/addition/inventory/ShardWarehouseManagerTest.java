package cloudy.autume.addition.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class ShardWarehouseManagerTest {
    @Test
    void parsesOnlyTheExactVisibleOwnedLoreFormat() {
        assertEquals(12_345, ShardWarehouseManager.parseOwnedLine("Owned: 12,345 Shards"));
        assertEquals(1, ShardWarehouseManager.parseOwnedLine("Owned: 1 Shard"));
        assertNull(ShardWarehouseManager.parseOwnedLine("Someone nearby owns 12,345 Shards"));
        assertNull(ShardWarehouseManager.parseOwnedLine("Owned: twelve Shards"));
    }
}
