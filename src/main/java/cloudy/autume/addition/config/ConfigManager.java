package cloudy.autume.addition.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("QCloudy_Addition/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("qcloudy_addition.json");
    private static final Path LEGACY_FILE = FabricLoader.getInstance().getConfigDir().resolve("autumecloudyaddition.json");
    private static ModConfig config = new ModConfig();

    private ConfigManager() {
    }

    public static ModConfig get() {
        return config;
    }

    public static void load() {
        Path source = Files.isRegularFile(FILE) ? FILE : LEGACY_FILE;
        if (!Files.isRegularFile(source)) {
            save();
            return;
        }
        try {
            ModConfig loaded = GSON.fromJson(Files.readString(source, StandardCharsets.UTF_8), ModConfig.class);
            if (loaded != null) {
                int previousVersion = loaded.configVersion;
                loaded.normalize();
                config = loaded;
                if (!source.equals(FILE) || previousVersion < loaded.configVersion) save();
            }
        } catch (Exception exception) {
            LOGGER.error("Could not read {}; using safe defaults", source, exception);
            config = new ModConfig();
        }
    }

    public static void save() {
        try {
            config.normalize();
            Files.createDirectories(FILE.getParent());
            Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(config), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.error("Could not save {}", FILE, exception);
        }
    }
}
