package cloudy.autume.addition.tracker;

import cloudy.autume.addition.config.ConfigManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class PetSkinTrackerTest {
    @BeforeEach
    void suppressDiskWrites() {
        ConfigManager.get().pets.rememberedDetails.clear();
        PetSkinTracker.setMemorySavesEnabledForTests(false);
    }

    @AfterEach
    void reset() {
        PetTracker.reset();
        PetSkinTracker.reset();
        ConfigManager.get().pets.rememberedDetails.clear();
        PetSkinTracker.setMemorySavesEnabledForTests(true);
    }

    @Test
    void extractsTextureHashFromReceivedProfileProperty() {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/abc123\"}}}";
        String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        assertEquals("abc123", PetSkinTracker.textureHash(encoded));
        assertEquals("", PetSkinTracker.textureHash("not base64"));
    }

    @Test
    void updatesAndRemovesTheCurrentPetsHeldItemFromReceivedChat() {
        PetTracker.updateFromChat("You summoned your Golden Dragon!");
        PetSkinTracker.onChat("Your pet is now holding Dwarf Turtle Shelmet.");
        assertEquals("Dwarf Turtle Shelmet",
                PetSkinTracker.currentDetails("Golden Dragon").heldItemId());

        PetSkinTracker.onChat("You removed Dwarf Turtle Shelmet from your pet!");
        assertEquals("", PetSkinTracker.currentDetails("Golden Dragon").heldItemId());
    }

    @Test
    void retainsAReceivedHeldItemAcrossSessionResetEvenAtMaxLevel() {
        PetTracker.updateFromTab(java.util.List.of(
                "Pet:", " [Lvl 100] Spinosaurus", " MAX LEVEL"));
        PetSkinTracker.onChat("Your pet is now holding Dwarf Turtle Shelmet.");

        PetSkinTracker.reset();

        assertEquals("Dwarf Turtle Shelmet",
                PetSkinTracker.currentDetails("Spinosaurus").heldItemId());
    }

    @Test
    void rejectsNearbyHeadsThatBelongToAnotherPetType() {
        assertEquals(true, PetSkinTracker.skinBelongsToPet("golden_dragon_ancient", "Golden Dragon"));
        assertEquals(false, PetSkinTracker.skinBelongsToPet("slime_spring", "Golden Dragon"));
        assertEquals(false, PetSkinTracker.skinBelongsToPet("jade_dragon_default", "Golden Dragon"));
    }
}
