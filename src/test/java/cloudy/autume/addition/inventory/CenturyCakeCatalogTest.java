package cloudy.autume.addition.inventory;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CenturyCakeCatalogTest {
    @Test
    void catalogContainsTwentyUniqueRealCakeIcons() {
        var cakes = CenturyCakeCatalog.instance().cakes();

        assertEquals(20, cakes.size());
        assertEquals(20, cakes.stream().map(CenturyCakeCatalog.Cake::internalId).distinct().count());
        assertEquals(20, cakes.stream().map(CenturyCakeCatalog.Cake::effect).distinct().count());
        for (CenturyCakeCatalog.Cake cake : cakes) {
            String decoded = new String(Base64.getDecoder().decode(cake.texture()), StandardCharsets.UTF_8);
            String url = JsonParser.parseString(decoded).getAsJsonObject()
                    .getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            assertTrue(url.startsWith("http://textures.minecraft.net/texture/"));
            assertEquals("UNCOMMON", cake.rarity());
        }
    }
}
