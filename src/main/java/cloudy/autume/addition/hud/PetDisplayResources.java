package cloudy.autume.addition.hud;

import com.google.common.collect.LinkedHashMultimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

final class PetDisplayResources {
    private static final String DATA_ROOT = "/assets/qcloudy_addition/data/";
    private static final Map<String, String> SKIN_NAMES = loadSkinNames();
    private static final Map<String, Accessory> ACCESSORIES = loadAccessories();
    private static final Map<String, Accessory> ACCESSORIES_BY_NAME = indexAccessoriesByName();

    private PetDisplayResources() {
    }

    static String skinName(String skinKey) {
        if (skinKey == null || skinKey.isBlank()) return "";
        String key = normalize(skinKey).replace("pet_skin_", "");
        return SKIN_NAMES.getOrDefault(key, titleCase(key) + " Skin");
    }

    static Accessory accessory(String itemIdOrName) {
        if (itemIdOrName == null || itemIdOrName.isBlank()) return null;
        Accessory byId = ACCESSORIES.get(itemIdOrName.trim().toUpperCase(Locale.ROOT));
        if (byId != null) return byId;
        return ACCESSORIES_BY_NAME.get(normalizeAccessoryName(itemIdOrName));
    }

    static Accessory accessoryInLines(List<String> lines) {
        for (String line : lines) {
            Accessory accessory = accessory(line);
            if (accessory != null) return accessory;
        }
        return null;
    }

    private static Map<String, String> loadSkinNames() {
        try (var stream = PetDisplayResources.class.getResourceAsStream(DATA_ROOT + "pet_skin_names.json")) {
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

    private static Map<String, Accessory> loadAccessories() {
        try (var stream = PetDisplayResources.class.getResourceAsStream(DATA_ROOT + "pet_accessories.json")) {
            if (stream == null) return Map.of();
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, Accessory> result = new HashMap<>();
                for (var entry : json.entrySet()) {
                    try {
                        JsonObject value = entry.getValue().getAsJsonObject();
                        Identifier baseId = Identifier.parse(value.get("base_item").getAsString());
                        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(baseId));
                        String model = value.get("item_model").getAsString();
                        if (!model.isBlank()) stack.set(DataComponents.ITEM_MODEL, Identifier.parse(model));
                        String texture = value.get("texture").getAsString();
                        if (!texture.isBlank()) stack.set(DataComponents.PROFILE, profile(texture));
                        result.put(entry.getKey(), new Accessory(value.get("name").getAsString(), stack));
                    } catch (RuntimeException ignored) {
                        // A future repo item must not disable every other accessory.
                    }
                }
                return Map.copyOf(result);
            }
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static Map<String, Accessory> indexAccessoriesByName() {
        Map<String, Accessory> result = new HashMap<>();
        ACCESSORIES.values().forEach(accessory -> result.put(normalizeAccessoryName(accessory.name()), accessory));
        return Map.copyOf(result);
    }

    static ResolvableProfile profile(String texture) {
        UUID uuid = UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8));
        var properties = LinkedHashMultimap.<String, Property>create();
        properties.put("textures", new Property("textures", texture, null));
        return ResolvableProfile.createResolved(new GameProfile(uuid, "", new PropertyMap(properties)));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    private static String normalizeAccessoryName(String value) {
        String normalized = value.trim();
        normalized = normalized.replaceFirst("(?i)^(?:held item|pet item|item)\\s*:\\s*", "");
        return normalized.toLowerCase(Locale.ROOT).replace('’', '\'').replaceAll("\\s+", " ");
    }

    private static String titleCase(String key) {
        StringBuilder result = new StringBuilder();
        for (String word : key.split("_")) {
            if (!result.isEmpty()) result.append(' ');
            if (!word.isEmpty()) result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    record Accessory(String name, ItemStack icon) {
    }
}
