package cloudy.autume.addition.i18n;

import cloudy.autume.addition.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public final class ModText {
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() { }.getType();
    private static final Map<String, String> ENGLISH = load("en_us");
    private static final Map<String, String> CHINESE = load("zh_cn");

    private ModText() {
    }

    public static String get(String key, Object... values) {
        Map<String, String> active = "zh_cn".equals(ConfigManager.get().language) ? CHINESE : ENGLISH;
        String template = active.getOrDefault(key, ENGLISH.getOrDefault(key, key));
        return values.length == 0 ? template : String.format(Locale.ROOT, template, values);
    }

    public static MutableComponent component(String key, Object... values) {
        return Component.literal(get(key, values));
    }

    private static Map<String, String> load(String language) {
        String path = "/assets/qcloudy_addition/lang/" + language + ".json";
        try (var stream = ModText.class.getResourceAsStream(path)) {
            if (stream == null) return Collections.emptyMap();
            try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                Map<String, String> result = new Gson().fromJson(reader, MAP_TYPE);
                return result == null ? Collections.emptyMap() : Map.copyOf(result);
            }
        } catch (IOException exception) {
            return Collections.emptyMap();
        }
    }
}
