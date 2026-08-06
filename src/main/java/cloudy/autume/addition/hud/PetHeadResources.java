package cloudy.autume.addition.hud;

import cloudy.autume.addition.tracker.PetSkinTracker;
import cloudy.autume.addition.tracker.PetTracker;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Builds a plain player head from a verified pet profile. Deliberately omits
 * synthetic petInfo/custom-data so another mod or resource pack cannot replace
 * this HUD icon with an unrelated pet item model.
 */
final class PetHeadResources {
    private static final String ROOT = "/assets/qcloudy_addition/data/";
    private static final Map<String, HeadData> PETS = loadPets();
    private static final Map<String, String> SKINS = loadStringMap("pet_skin_profiles.json");

    private PetHeadResources() {
    }

    static String baseTexture(String petName) {
        HeadData data = PETS.get(normalize(petName));
        return data == null ? "" : data.texture();
    }

    static String skinTexture(String skinKey) {
        return skinKey == null ? "" : SKINS.getOrDefault(normalizeSkin(skinKey), "");
    }

    static ItemStack stack(PetTracker.PetSnapshot pet, String skinKey, double exactExperience) {
        HeadData base = PETS.get(normalize(pet.name()));
        if (base == null) return ItemStack.EMPTY;
        ResolvableProfile profile = PetSkinTracker.currentProfile(pet.name());
        if (profile == null && skinKey != null && !skinKey.isBlank()) {
            String encoded = skinTexture(skinKey);
            if (encoded != null) profile = PetDisplayResources.profile(encoded);
        }
        if (profile == null) profile = PetDisplayResources.profile(base.texture());

        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("player_head"));
        stack.set(DataComponents.PROFILE, profile);
        return stack;
    }

    private static Map<String, HeadData> loadPets() {
        try (var stream = PetHeadResources.class.getResourceAsStream(ROOT + "pet_head_profiles.json")) {
            if (stream == null) return Map.of();
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, HeadData> result = new HashMap<>();
                for (var entry : json.entrySet()) {
                    JsonObject value = entry.getValue().getAsJsonObject();
                    result.put(entry.getKey(), new HeadData(
                            value.get("id").getAsString(), value.get("texture").getAsString()));
                }
                return Map.copyOf(result);
            }
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static Map<String, String> loadStringMap(String file) {
        try (var stream = PetHeadResources.class.getResourceAsStream(ROOT + file)) {
            if (stream == null) return Map.of();
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, String> result = new HashMap<>();
                json.entrySet().forEach(entry -> result.put(entry.getKey(), entry.getValue().getAsString()));
                return Map.copyOf(result);
            }
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    private static String normalizeSkin(String value) {
        return normalize(value).replace("pet_skin_", "");
    }

    private record HeadData(String id, String texture) {
    }
}
