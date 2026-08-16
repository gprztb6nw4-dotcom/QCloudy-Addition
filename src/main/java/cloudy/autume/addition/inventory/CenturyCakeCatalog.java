package cloudy.autume.addition.inventory;

import com.google.common.collect.LinkedHashMultimap;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Offline, versioned metadata for all twenty Century Cakes. */
public final class CenturyCakeCatalog {
    private static final String RESOURCE = "/assets/qcloudy_addition/data/century_cakes.json";
    private static final CenturyCakeCatalog INSTANCE = load();
    private final List<Cake> cakes;
    private final Map<String, Cake> byId;

    private CenturyCakeCatalog(List<Cake> cakes) {
        this.cakes = List.copyOf(cakes);
        Map<String, Cake> indexed = new LinkedHashMap<>();
        cakes.forEach(cake -> indexed.put(cake.internalId(), cake));
        this.byId = Map.copyOf(indexed);
    }

    public static CenturyCakeCatalog instance() {
        return INSTANCE;
    }

    public List<Cake> cakes() {
        return cakes;
    }

    public Optional<Cake> byId(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    Cake matchEffectText(String text) {
        String normalized = normalize(text);
        return cakes.stream()
                .sorted(Comparator.comparingInt((Cake cake) -> cake.effect().length()).reversed())
                .filter(cake -> normalized.contains(normalize(cake.effect())))
                .findFirst().orElse(null);
    }

    private static CenturyCakeCatalog load() {
        try (var stream = CenturyCakeCatalog.class.getResourceAsStream(RESOURCE)) {
            if (stream == null) throw new IllegalStateException("Missing " + RESOURCE);
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                var root = JsonParser.parseReader(reader).getAsJsonObject();
                List<Cake> cakes = new ArrayList<>();
                for (var element : root.getAsJsonArray("cakes")) {
                    var value = element.getAsJsonObject();
                    cakes.add(new Cake(
                            value.get("internalId").getAsString(),
                            value.get("name").getAsString(),
                            value.get("effect").getAsString(),
                            value.get("bonus").getAsString(),
                            value.get("rarity").getAsString(),
                            value.get("texture").getAsString()));
                }
                if (cakes.size() != 20 || cakes.stream().map(Cake::internalId).distinct().count() != 20
                        || cakes.stream().map(Cake::effect).distinct().count() != 20) {
                    throw new IllegalStateException("Century Cake catalog must contain 20 unique cakes and effects");
                }
                return new CenturyCakeCatalog(cakes);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Could not load Century Cake catalog", exception);
        }
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }

    public record Cake(String internalId, String name, String effect, String bonus,
                       String rarity, String texture) {
        public ItemStack icon() {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            stack.set(DataComponents.ITEM_MODEL, Identifier.withDefaultNamespace("player_head"));
            UUID uuid = UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8));
            var properties = LinkedHashMultimap.<String, Property>create();
            properties.put("textures", new Property("textures", texture, null));
            stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(
                    new GameProfile(uuid, "", new PropertyMap(properties))));
            return stack;
        }
    }
}
