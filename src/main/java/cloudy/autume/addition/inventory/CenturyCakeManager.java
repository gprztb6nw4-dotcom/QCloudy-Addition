package cloudy.autume.addition.inventory;

import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.i18n.ModText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.sounds.SoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Remembers client-received cake refreshes per account/profile and warns once
 * when their real-world 48-hour duration ends.
 */
public final class CenturyCakeManager {
    static final long DURATION_MS = 48L * 60L * 60L * 1_000L;
    private static final int MAX_PROFILES = 64;
    private static final Logger LOGGER = LoggerFactory.getLogger("QCloudy_Addition/CenturyCakes");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("qcloudy_addition_century_cakes.json");
    private static CakeFile data = new CakeFile();

    private CenturyCakeManager() {
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        try {
            CakeFile loaded = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), CakeFile.class);
            if (loaded != null) data = normalize(loaded);
        } catch (Exception exception) {
            LOGGER.warn("Could not read {}; starting with empty Century Cake timers", FILE, exception);
            data = new CakeFile();
        }
    }

    public static void onMessage(Component message, boolean overlay) {
        if (overlay || message == null) return;
        CenturyCakeCatalog.Cake cake = CenturyCakeParser.parse(message.getString());
        if (cake == null) return;
        Minecraft client = Minecraft.getInstance();
        long now = System.currentTimeMillis();
        ProfileState profile = data.profiles.computeIfAbsent(ProfileContext.key(client), ignored -> new ProfileState());
        profile.effects.put(cake.internalId(), new EffectState(now + DURATION_MS, false));
        profile.updatedAt = now;
        trimProfiles();
        save();
    }

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        ProfileState profile = data.profiles.get(ProfileContext.key(client));
        if (profile == null) return;
        long now = System.currentTimeMillis();
        List<CenturyCakeCatalog.Cake> expired = collectExpired(profile, now);
        if (expired.isEmpty()) return;
        profile.updatedAt = now;
        save();
        if (!ConfigManager.get().centuryCakes.expiryAlerts) return;
        alert(client, expired);
    }

    public static List<CakeStatus> current(Minecraft client) {
        ProfileState profile = data.profiles.get(ProfileContext.key(client));
        long now = System.currentTimeMillis();
        List<CakeStatus> result = new ArrayList<>();
        for (CenturyCakeCatalog.Cake cake : CenturyCakeCatalog.instance().cakes()) {
            EffectState state = profile == null ? null : profile.effects.get(cake.internalId());
            long expiry = state == null ? 0L : Math.max(0L, state.expiresAt);
            result.add(new CakeStatus(cake, expiry, Math.max(0L, expiry - now)));
        }
        return List.copyOf(result);
    }

    static List<CenturyCakeCatalog.Cake> collectExpired(ProfileState profile, long now) {
        List<CenturyCakeCatalog.Cake> expired = new ArrayList<>();
        for (Map.Entry<String, EffectState> entry : profile.effects.entrySet()) {
            EffectState state = entry.getValue();
            if (state == null || state.alerted || state.expiresAt <= 0L || state.expiresAt > now) continue;
            state.alerted = true;
            CenturyCakeCatalog.instance().byId(entry.getKey()).ifPresent(expired::add);
        }
        expired.sort(Comparator.comparing(CenturyCakeCatalog.Cake::effect));
        return expired;
    }

    private static void alert(Minecraft client, List<CenturyCakeCatalog.Cake> expired) {
        String titleText = titleText(expired);
        MinecraftClientCompat.showTitle(client,
                Component.literal(titleText).withStyle(ChatFormatting.BOLD, ChatFormatting.RED),
                Component.empty(), 6, 50, 10);

        client.gui.getChat().addClientSystemMessage(chatMessage(expired));

        var audio = ConfigManager.get().centuryCakes.expiryAudio;
        if (audio.sound && audio.volume > 0) {
            client.player.playSound(SoundEvents.NOTE_BLOCK_PLING.value(), audio.volume / 100.0f, 1.1f);
        }
    }

    static String titleText(List<CenturyCakeCatalog.Cake> expired) {
        if (expired.size() == 1) {
            return ModText.get("century_cake.expired.single", expired.getFirst().effect());
        }
        return ModText.get("century_cake.expired.multiple", expired.size());
    }

    static Component chatMessage(List<CenturyCakeCatalog.Cake> expired) {
        var prefix = Component.literal("[QC] ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(titleText(expired) + " ").withStyle(ChatFormatting.RED));
        Component visit = Component.literal(ModText.get("century_cake.visit"))
                .withStyle(style -> style.withColor(ChatFormatting.YELLOW).withUnderlined(true)
                        .withClickEvent(new ClickEvent.RunCommand("/visit northwestcloudy"))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Component.literal(ModText.get("century_cake.visit.hover")))));
        return prefix.append(visit);
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(data), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.warn("Could not save {}", FILE, exception);
        }
    }

    private static CakeFile normalize(CakeFile source) {
        CakeFile repaired = new CakeFile();
        if (source.profiles == null) return repaired;
        for (Map.Entry<String, ProfileState> profileEntry : source.profiles.entrySet()) {
            if (profileEntry.getKey() == null || profileEntry.getValue() == null
                    || repaired.profiles.size() >= MAX_PROFILES) continue;
            ProfileState profile = new ProfileState();
            profile.updatedAt = Math.max(0L, profileEntry.getValue().updatedAt);
            if (profileEntry.getValue().effects != null) {
                for (Map.Entry<String, EffectState> effectEntry : profileEntry.getValue().effects.entrySet()) {
                    EffectState state = effectEntry.getValue();
                    if (state == null || CenturyCakeCatalog.instance().byId(effectEntry.getKey()).isEmpty()) continue;
                    profile.effects.put(effectEntry.getKey(),
                            new EffectState(Math.max(0L, state.expiresAt), state.alerted));
                }
            }
            repaired.profiles.put(profileEntry.getKey(), profile);
        }
        return repaired;
    }

    private static void trimProfiles() {
        while (data.profiles.size() > MAX_PROFILES) {
            String oldest = data.profiles.entrySet().stream()
                    .min(Map.Entry.comparingByValue((left, right) -> Long.compare(left.updatedAt, right.updatedAt)))
                    .map(Map.Entry::getKey).orElse(null);
            if (oldest == null) break;
            data.profiles.remove(oldest);
        }
    }

    public record CakeStatus(CenturyCakeCatalog.Cake cake, long expiresAt, long remainingMs) {
        public boolean active() {
            return remainingMs > 0L;
        }
    }

    @SuppressWarnings("unused")
    static final class CakeFile {
        int schemaVersion = 1;
        Map<String, ProfileState> profiles = new LinkedHashMap<>();
    }

    @SuppressWarnings("unused")
    static final class ProfileState {
        long updatedAt;
        Map<String, EffectState> effects = new LinkedHashMap<>();
    }

    @SuppressWarnings("unused")
    static final class EffectState {
        long expiresAt;
        boolean alerted;

        EffectState() {
        }

        EffectState(long expiresAt, boolean alerted) {
            this.expiresAt = expiresAt;
            this.alerted = alerted;
        }
    }
}
