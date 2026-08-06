package cloudy.autume.addition.inventory;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SkyBlockItemDataTest {
    @Test
    void readsNestedUuidIdAndNumericTimestamp() {
        CompoundTag extra = new CompoundTag();
        extra.putString("id", "ASPECT_OF_THE_VOID");
        extra.putString("uuid", "123e4567-e89b-12d3-a456-426614174000");
        extra.putLong("timestamp", 1_700_000_000_000L);

        assertEquals("ASPECT_OF_THE_VOID", SkyBlockItemData.itemId(extra));
        assertEquals("123e4567e89b12d3a456426614174000", SkyBlockItemData.uuid(extra));
        assertEquals(Instant.ofEpochMilli(1_700_000_000_000L), SkyBlockItemData.timestamp(extra));
    }

    @Test
    void identifiesHuntingToolkitWithoutDependingOnItsChangingUuid() {
        CompoundTag extra = new CompoundTag();
        extra.putString("id", "HUNTING_TOOLKIT");
        assertTrue(SkyBlockItemData.isHuntingBox(extra));
    }
}
