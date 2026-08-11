package cloudy.autume.addition.config;

import cloudy.autume.addition.i18n.ModText;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.joml.Vector2i;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.BooleanSupplier;

/**
 * Optional, reflection-only adapters for supported SkyBlock mods.
 *
 * <p>No external mod is a compile or runtime dependency. An adapter is only
 * enabled for the exact version it was audited against. Reads and writes go
 * through the installed mod's live configuration object and save hook; this
 * deliberately avoids editing another mod's JSON file behind its back.</p>
 */
final class UnifiedModIntegration {
    private static final int MAX_SCAN_DEPTH = 5;
    private static final int MAX_SETTINGS_PER_FEATURE = 48;
    private static volatile List<UnifiedFeature> cached;

    private UnifiedModIntegration() { }

    enum Provider {
        QCLOUDY("qcloudy_addition", "QCloudy", "2.6.13"),
        SKYHANNI("skyhanni", "SkyHanni", "7.41.0"),
        SKYBLOCKER("skyblocker", "SkyBlocker", "6.8.2+26.1.2"),
        FIRMAMENT("firmament", "Firmament", "44.3.0+mc26.1.2"),
        BABYZOMBIE("babyzombieaddons", "BabyZombieAddons", "3.4.1");

        final String modId;
        final String displayName;
        final String auditedVersion;

        Provider(String modId, String displayName, String auditedVersion) {
            this.modId = modId;
            this.displayName = displayName;
            this.auditedVersion = auditedVersion;
        }
    }

    enum ValueKind { BOOLEAN, INTEGER, DECIMAL, ENUM, STRING, UNSUPPORTED }

    static final class NativeSetting {
        final String id;
        final String label;
        final ValueKind kind;
        final @Nullable Double minimum;
        final @Nullable Double maximum;
        private final ValueAccess access;

        NativeSetting(String id, String label, ValueKind kind, @Nullable Double minimum,
                      @Nullable Double maximum, ValueAccess access) {
            this.id = id;
            this.label = label;
            this.kind = kind;
            this.minimum = minimum;
            this.maximum = maximum;
            this.access = access;
        }

        @Nullable Object value() {
            try {
                return access.get();
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }

        boolean set(@Nullable Object value) {
            try {
                access.set(value);
                return true;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return false;
            }
        }

        String displayValue() {
            Object value = value();
            if (value == null) return ModText.get("config.integration.unavailable");
            if (value instanceof Boolean bool) {
                return ModText.get(bool ? "config.enabled" : "config.disabled");
            }
            if (value instanceof Number number) {
                if (kind == ValueKind.INTEGER) return Long.toString(number.longValue());
                return String.format(Locale.ROOT, "%.2f", number.doubleValue())
                        .replaceAll("0+$", "").replaceAll("\\.$", "");
            }
            if (value instanceof Enum<?> enumeration) return humanize(enumeration.name());
            return value.toString();
        }

        boolean toggleOrCycle() {
            Object current = value();
            if (current instanceof Boolean bool) return set(!bool);
            if (current instanceof Enum<?> enumeration) {
                Object[] constants = enumeration.getDeclaringClass().getEnumConstants();
                if (constants == null || constants.length == 0) return false;
                return set(constants[(enumeration.ordinal() + 1) % constants.length]);
            }
            return false;
        }

        double sliderFraction() {
            Object current = value();
            if (!(current instanceof Number number) || minimum == null || maximum == null
                    || maximum <= minimum) return 0.0;
            return Math.clamp((number.doubleValue() - minimum) / (maximum - minimum), 0.0, 1.0);
        }

        boolean setSliderFraction(double fraction) {
            if (minimum == null || maximum == null) return false;
            double number = minimum + Math.clamp(fraction, 0.0, 1.0) * (maximum - minimum);
            return set(kind == ValueKind.INTEGER ? (int) Math.round(number) : number);
        }

        boolean editable() {
            return kind == ValueKind.BOOLEAN || kind == ValueKind.ENUM
                    || ((kind == ValueKind.INTEGER || kind == ValueKind.DECIMAL)
                    && minimum != null && maximum != null && maximum > minimum);
        }
    }

    static final class NativeFeature {
        final Provider provider;
        final String id;
        final String title;
        final String description;
        final ConfigScreen.Category category;
        final String group;
        final NativeSetting primary;
        final List<NativeSetting> settings;

        NativeFeature(Provider provider, String id, String title, String description,
                      ConfigScreen.Category category, String group, NativeSetting primary,
                      List<NativeSetting> settings) {
            this.provider = provider;
            this.id = id;
            this.title = title;
            this.description = description;
            this.category = category;
            this.group = group;
            this.primary = primary;
            this.settings = List.copyOf(settings);
        }

        boolean enabled() {
            Object value = primary.value();
            if (value instanceof Boolean bool) return bool;
            if (value instanceof Enum<?> enumeration) {
                String name = enumeration.name().toUpperCase(Locale.ROOT);
                return !Set.of("OFF", "DISABLED", "NONE", "HIDDEN").contains(name);
            }
            return value != null;
        }

        boolean setEnabled(boolean enabled) {
            Object current = primary.value();
            if (current instanceof Boolean) return primary.set(enabled);
            if (current instanceof Enum<?> enumeration) {
                Object[] values = enumeration.getDeclaringClass().getEnumConstants();
                if (values == null || values.length == 0) return false;
                Object disabled = values[0];
                Object active = values[0];
                for (Object value : values) {
                    String name = ((Enum<?>) value).name().toUpperCase(Locale.ROOT);
                    if (Set.of("OFF", "DISABLED", "NONE", "HIDDEN").contains(name)) disabled = value;
                    else if (active == values[0]) active = value;
                }
                return primary.set(enabled ? active : disabled);
            }
            return false;
        }
    }

    static final class UnifiedFeature {
        final String id;
        final String title;
        final String description;
        final ConfigScreen.Category category;
        final String group;
        final ConfigScreen.@Nullable Feature qcloudyFeature;
        final List<NativeFeature> external;

        UnifiedFeature(String id, String title, String description, ConfigScreen.Category category,
                       String group, ConfigScreen.@Nullable Feature qcloudyFeature,
                       List<NativeFeature> external) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.category = category;
            this.group = group;
            this.qcloudyFeature = qcloudyFeature;
            this.external = List.copyOf(external);
        }

        List<Provider> providers() {
            List<Provider> providers = new ArrayList<>();
            if (qcloudyFeature != null) providers.add(Provider.QCLOUDY);
            for (NativeFeature feature : external) {
                if (!providers.contains(feature.provider)) providers.add(feature.provider);
            }
            return List.copyOf(providers);
        }

        Provider selectedProvider() {
            List<Provider> available = providers();
            if (available.isEmpty()) return Provider.QCLOUDY;
            String saved = ConfigManager.get().integrations.selectedProviders.get(id);
            if (saved != null) {
                try {
                    Provider provider = Provider.valueOf(saved);
                    if (available.contains(provider)) return provider;
                } catch (IllegalArgumentException ignored) { }
            }
            return available.getFirst();
        }

        void selectProvider(Provider provider) {
            if (!providers().contains(provider)) return;
            ConfigManager.get().integrations.selectedProviders.put(id, provider.name());
            ConfigManager.save();
        }

        Provider cycleProvider() {
            List<Provider> values = providers();
            if (values.isEmpty()) return Provider.QCLOUDY;
            int index = values.indexOf(selectedProvider());
            Provider result = values.get((index + 1) % values.size());
            selectProvider(result);
            return result;
        }

        boolean enabled() {
            Provider selected = selectedProvider();
            if (selected == Provider.QCLOUDY && qcloudyFeature != null) {
                return qcloudyFeature.enabled(ConfigManager.get());
            }
            NativeFeature binding = binding(selected);
            return binding != null && binding.enabled();
        }

        boolean toggle() {
            boolean next = !enabled();
            Provider selected = selectedProvider();
            if (next) {
                // Only providers attached to this exact logical feature are
                // mutually exclusive. Related price/profit features remain independent.
                if (qcloudyFeature != null && selected != Provider.QCLOUDY
                        && qcloudyFeature.enabled(ConfigManager.get())) qcloudyFeature.toggle(ConfigManager.get());
                for (NativeFeature binding : external) {
                    if (binding.provider != selected && binding.enabled()) binding.setEnabled(false);
                }
            }
            boolean changed;
            if (selected == Provider.QCLOUDY && qcloudyFeature != null) {
                if (qcloudyFeature.enabled(ConfigManager.get()) != next) qcloudyFeature.toggle(ConfigManager.get());
                changed = true;
            } else {
                NativeFeature binding = binding(selected);
                changed = binding != null && binding.setEnabled(next);
            }
            ConfigManager.save();
            return changed;
        }

        @Nullable NativeFeature binding(Provider provider) {
            for (NativeFeature feature : external) if (feature.provider == provider) return feature;
            return null;
        }
    }

    /**
     * A movable HUD owned by the currently selected external provider.
     *
     * <p>The editor never invents a separate QCloudy position. It writes the
     * provider's own audited live x/y/scale values, so reopening that mod's
     * native editor shows the same result.</p>
     */
    static final class ExternalHud {
        final UnifiedFeature feature;
        final NativeFeature binding;
        final NativeSetting x;
        final NativeSetting y;
        final @Nullable NativeSetting scale;

        ExternalHud(UnifiedFeature feature, NativeFeature binding, NativeSetting x,
                    NativeSetting y, @Nullable NativeSetting scale) {
            this.feature = feature;
            this.binding = binding;
            this.x = x;
            this.y = y;
            this.scale = scale;
        }

        String id() {
            // One native HUD can be surfaced by more than one Boolean option
            // in a provider config. The audited position path is the stable
            // identity; including the feature binding would duplicate the
            // same live panel in QCA's editor.
            return binding.provider.name() + ":" + x.id;
        }

        String label() {
            return binding.provider.displayName + " · " + feature.title;
        }

        int x() {
            Object value = x.value();
            return value instanceof Number number ? number.intValue() : 0;
        }

        int y() {
            Object value = y.value();
            return value instanceof Number number ? number.intValue() : 0;
        }

        float scale() {
            Object value = scale == null ? null : scale.value();
            return value instanceof Number number ? Math.clamp(number.floatValue(), 0.25f, 4.0f) : 1.0f;
        }

        boolean setPosition(int newX, int newY) {
            return x.set(newX) && y.set(newY);
        }

        boolean setScale(float newScale) {
            return scale != null && scale.set(Math.clamp(newScale, 0.25f, 4.0f));
        }
    }

    static List<UnifiedFeature> features() {
        List<UnifiedFeature> result = cached;
        if (result != null) return result;
        synchronized (UnifiedModIntegration.class) {
            if (cached == null) cached = buildFeatures();
            return cached;
        }
    }

    static void invalidate() {
        cached = null;
    }

    static @Nullable UnifiedFeature forQCloudy(ConfigScreen.Feature feature) {
        for (UnifiedFeature unified : features()) if (unified.qcloudyFeature == feature) return unified;
        return null;
    }

    static List<ExternalHud> externalHuds() {
        Map<String, ExternalHud> result = new LinkedHashMap<>();
        for (UnifiedFeature feature : features()) {
            Provider selected = feature.selectedProvider();
            if (selected == Provider.QCLOUDY || !feature.enabled()) continue;
            NativeFeature binding = feature.binding(selected);
            if (binding == null) continue;
            NativeSetting x = hudSetting(binding.settings, "x");
            NativeSetting y = hudSetting(binding.settings, "y");
            if (x == null || y == null || x.value() == null || y.value() == null) continue;
            NativeSetting scale = hudSetting(binding.settings, "scale");
            ExternalHud hud = new ExternalHud(feature, binding, x, y, scale);
            result.putIfAbsent(hud.id(), hud);
        }
        for (ExternalHud hud : babyZombieHuds()) result.putIfAbsent(hud.id(), hud);
        return List.copyOf(result.values());
    }

    private static List<ExternalHud> babyZombieHuds() {
        if (!providerCompatible(Provider.BABYZOMBIE)) return List.of();
        try {
            Class<?> manager = Class.forName("top.babyzombie.addons.config.hud.HudManager");
            Field elementsField = manager.getDeclaredField("elements");
            elementsField.setAccessible(true);
            Map<?, ?> elements = (Map<?, ?>) elementsField.get(null);
            List<ExternalHud> result = new ArrayList<>();
            for (Map.Entry<?, ?> entry : elements.entrySet()) {
                Object element = entry.getValue();
                Field showField = findField(element.getClass(), "showCondition");
                showField.setAccessible(true);
                Object show = showField.get(element);
                if (!(show instanceof BooleanSupplier supplier) || !supplier.getAsBoolean()) continue;
                String name = String.valueOf(entry.getKey());
                NativeSetting primary = new NativeSetting("hud." + name + ".visible", humanize(name),
                        ValueKind.BOOLEAN, null, null, new ValueAccess() {
                    @Override public Object get() {
                        return supplier.getAsBoolean();
                    }

                    @Override public void set(@Nullable Object value) {
                        throw new UnsupportedOperationException("Visibility is owned by the feature config");
                    }
                });
                List<NativeSetting> settings = List.of(
                        babyZombieHudField(manager, element, name, "x", ValueKind.INTEGER, -4096.0, 4096.0),
                        babyZombieHudField(manager, element, name, "y", ValueKind.INTEGER, -4096.0, 4096.0),
                        babyZombieHudField(manager, element, name, "scale", ValueKind.DECIMAL, 0.25, 4.0)
                );
                ConfigScreen.Category category = classify(name);
                NativeFeature binding = new NativeFeature(Provider.BABYZOMBIE,
                        canonicalId(category, humanize(name)), humanize(name),
                        Provider.BABYZOMBIE.displayName + " HUD", category, groupName(name, "HUD"),
                        primary, settings);
                UnifiedFeature feature = new UnifiedFeature(binding.id, binding.title, binding.description,
                        category, binding.group, null, List.of(binding));
                result.add(new ExternalHud(feature, binding, settings.get(0), settings.get(1), settings.get(2)));
            }
            return List.copyOf(result);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return List.of();
        }
    }

    private static NativeSetting babyZombieHudField(Class<?> manager, Object element, String name,
                                                     String fieldName, ValueKind kind,
                                                     double minimum, double maximum) {
        return new NativeSetting("hud." + name + "." + fieldName,
                fieldName.equals("scale") ? ModText.get("config.setting.scale")
                        : fieldName.toUpperCase(Locale.ROOT),
                kind, minimum, maximum, new ValueAccess() {
            @Override public Object get() throws ReflectiveOperationException {
                Field field = findField(element.getClass(), fieldName);
                field.setAccessible(true);
                return field.get(element);
            }

            @Override public void set(@Nullable Object value) throws ReflectiveOperationException {
                Field field = findField(element.getClass(), fieldName);
                field.setAccessible(true);
                field.set(element, coerce(value, field.getType()));
                Method save = manager.getDeclaredMethod("save");
                save.setAccessible(true);
                save.invoke(null);
            }
        });
    }

    private static @Nullable NativeSetting hudSetting(List<NativeSetting> settings, String suffix) {
        for (NativeSetting setting : settings) {
            String normalized = setting.id.toLowerCase(Locale.ROOT);
            if (normalized.endsWith("." + suffix)) return setting;
        }
        return null;
    }

    private static List<UnifiedFeature> buildFeatures() {
        List<NativeFeature> nativeFeatures = new ArrayList<>();
        for (Adapter adapter : adapters()) {
            if (!adapter.compatible()) continue;
            try {
                nativeFeatures.addAll(adapter.discover());
            } catch (ReflectiveOperationException | RuntimeException ignored) { }
        }

        Map<String, MutableUnified> merged = new LinkedHashMap<>();
        for (ConfigScreen.Feature feature : ConfigScreen.Feature.values()) {
            String id = canonicalId(feature.category, ModText.get(feature.titleKey));
            MutableUnified entry = merged.computeIfAbsent(id, ignored -> new MutableUnified(id));
            entry.local = feature;
            entry.title = ModText.get(feature.titleKey);
            entry.description = ModText.get(feature.descriptionKey);
            entry.category = feature.category;
            entry.group = ModText.get(feature.group.key);
        }
        for (NativeFeature feature : nativeFeatures) {
            String id = alias(canonicalId(feature.category, feature.title));
            MutableUnified entry = merged.computeIfAbsent(id, ignored -> new MutableUnified(id));
            if (entry.title == null) entry.title = feature.title;
            if (entry.description == null) entry.description = feature.description;
            if (entry.category == null) entry.category = feature.category;
            if (entry.group == null) entry.group = feature.group;
            entry.external.add(feature);
        }
        List<UnifiedFeature> features = new ArrayList<>();
        for (MutableUnified value : merged.values()) {
            if (value.title == null || value.category == null) continue;
            features.add(new UnifiedFeature(value.id, value.title,
                    value.description == null ? "" : value.description, value.category,
                    value.group == null ? ModText.get("config.group.integrations") : value.group,
                    value.local, value.external));
        }
        features.sort(Comparator.comparing((UnifiedFeature feature) -> feature.category.ordinal())
                .thenComparing(feature -> feature.group, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(feature -> feature.title, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(features);
    }

    private static List<Adapter> adapters() {
        return List.of(
                new ObjectGraphAdapter(Provider.SKYHANNI,
                        "at.hannibal2.skyhanni.SkyHanniMod", "feature", null, "saveNow"),
                new SkyBlockerAdapter(),
                new ObjectGraphAdapter(Provider.BABYZOMBIE,
                        "top.babyzombie.addons.config.ModConfigManager", null, "get", "save"),
                new FirmamentAdapter()
        );
    }

    private static boolean providerCompatible(Provider provider) {
        ModContainer container = FabricLoader.getInstance().getModContainer(provider.modId).orElse(null);
        if (container == null) return false;
        String version = container.getMetadata().getVersion().getFriendlyString();
        return version.equals(provider.auditedVersion)
                || version.startsWith(provider.auditedVersion + "+")
                || provider.auditedVersion.startsWith(version + "+");
    }

    private interface Adapter {
        Provider provider();
        boolean compatible();
        List<NativeFeature> discover() throws ReflectiveOperationException;
    }

    private abstract static class BaseAdapter implements Adapter {
        final Provider provider;

        BaseAdapter(Provider provider) {
            this.provider = provider;
        }

        @Override
        public Provider provider() {
            return provider;
        }

        @Override
        public boolean compatible() {
            return providerCompatible(provider);
        }
    }

    private static class ObjectGraphAdapter extends BaseAdapter {
        private final String rootClass;
        private final @Nullable String staticField;
        private final @Nullable String staticGetter;
        private final @Nullable String saveMethod;

        ObjectGraphAdapter(Provider provider, String rootClass, @Nullable String staticField,
                           @Nullable String staticGetter, @Nullable String saveMethod) {
            super(provider);
            this.rootClass = rootClass;
            this.staticField = staticField;
            this.staticGetter = staticGetter;
            this.saveMethod = saveMethod;
        }

        @Override
        public List<NativeFeature> discover() throws ReflectiveOperationException {
            Object root = root();
            List<NativeFeature> result = new ArrayList<>();
            scanObject(this, root, List.of(), root.getClass().getSimpleName(), 0,
                    Collections.newSetFromMap(new IdentityHashMap<>()), result);
            return result;
        }

        Object root() throws ReflectiveOperationException {
            Class<?> type = Class.forName(rootClass);
            if (staticField != null) {
                Field field = type.getDeclaredField(staticField);
                field.setAccessible(true);
                return field.get(null);
            }
            Method method = type.getMethod(staticGetter);
            return method.invoke(null);
        }

        Object read(List<String> path) throws ReflectiveOperationException {
            return readPath(root(), path);
        }

        void write(List<String> path, @Nullable Object value) throws ReflectiveOperationException {
            Object root = root();
            writePath(root, path, value);
            save(root);
        }

        void save(Object root) throws ReflectiveOperationException {
            if (saveMethod == null) return;
            try {
                root.getClass().getMethod(saveMethod).invoke(root);
            } catch (NoSuchMethodException ignored) {
                Class<?> type = Class.forName(rootClass);
                type.getMethod(saveMethod).invoke(null);
            }
        }
    }

    private static final class SkyBlockerAdapter extends ObjectGraphAdapter {
        SkyBlockerAdapter() {
            super(Provider.SKYBLOCKER, "de.hysky.skyblocker.config.SkyblockerConfigManager",
                    null, "get", null);
        }

        @Override
        void write(List<String> path, @Nullable Object value) throws ReflectiveOperationException {
            Class<?> manager = Class.forName("de.hysky.skyblocker.config.SkyblockerConfigManager");
            Method update = manager.getMethod("update", Consumer.class);
            Consumer<Object> action = root -> {
                try {
                    writePath(root, path, value);
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException(exception);
                }
            };
            update.invoke(null, action);
        }
    }

    private static final class FirmamentAdapter extends BaseAdapter {
        FirmamentAdapter() {
            super(Provider.FIRMAMENT);
        }

        @Override
        public List<NativeFeature> discover() throws ReflectiveOperationException {
            Class<?> managedConfig = Class.forName("moe.nea.firmament.util.data.ManagedConfig");
            Object companion = managedConfig.getField("Companion").get(null);
            Object instanceList = companion.getClass().getMethod("getAllManagedConfigs").invoke(companion);
            Collection<?> configs = (Collection<?>) instanceList.getClass().getMethod("getAll").invoke(instanceList);
            List<NativeFeature> result = new ArrayList<>();
            for (Object config : configs) {
                String configName = String.valueOf(config.getClass().getMethod("getName").invoke(config));
                String categoryName = String.valueOf(config.getClass().getMethod("getCategory").invoke(config));
                Map<?, ?> options = (Map<?, ?>) config.getClass().getMethod("getAllOptions").invoke(config);
                for (Object option : options.values()) {
                    Object value = option.getClass().getMethod("get").invoke(option);
                    ValueKind kind = valueKind(value == null ? Object.class : value.getClass());
                    if (kind != ValueKind.BOOLEAN && kind != ValueKind.ENUM) continue;
                    String property = String.valueOf(option.getClass().getMethod("getPropertyName").invoke(option));
                    String title = componentString(option, "getLabelText", humanize(property));
                    String description = componentString(option, "getLabelDescription",
                            provider.displayName + " · " + configName);
                    ValueAccess access = new ValueAccess() {
                        @Override public Object get() throws ReflectiveOperationException {
                            return option.getClass().getMethod("get").invoke(option);
                        }

                        @Override public void set(@Nullable Object newValue) throws ReflectiveOperationException {
                            option.getClass().getMethod("set", Object.class).invoke(option, newValue);
                            markFirmamentDirty(config);
                        }
                    };
                    NativeSetting primary = new NativeSetting(configName + "." + property, title, kind,
                            null, null, access);
                    List<NativeSetting> settings = new ArrayList<>();
                    for (Object sibling : options.values()) {
                        if (sibling == option || settings.size() >= MAX_SETTINGS_PER_FEATURE) continue;
                        List<NativeSetting> hudSettings = firmamentHudSettings(config, configName, sibling);
                        if (!hudSettings.isEmpty()) {
                            for (NativeSetting setting : hudSettings) {
                                if (settings.size() >= MAX_SETTINGS_PER_FEATURE) break;
                                settings.add(setting);
                            }
                            continue;
                        }
                        NativeSetting setting = firmamentSetting(config, configName, sibling);
                        if (setting != null) settings.add(setting);
                    }
                    String path = categoryName + "." + configName + "." + property;
                    ConfigScreen.Category category = classify(path);
                    result.add(new NativeFeature(provider, canonicalId(category, title), title, description,
                            category, groupName(path, configName), primary, settings));
                }
            }
            return result;
        }

        private @Nullable NativeSetting firmamentSetting(Object config, String configName, Object option) {
            try {
                Object value = option.getClass().getMethod("get").invoke(option);
                if (value != null && value.getClass().getName().equals("moe.nea.firmament.gui.config.HudMeta")) {
                    return null;
                }
                ValueKind kind = valueKind(value == null ? Object.class : value.getClass());
                if (kind == ValueKind.UNSUPPORTED) return null;
                String property = String.valueOf(option.getClass().getMethod("getPropertyName").invoke(option));
                String label = componentString(option, "getLabelText", humanize(property));
                ValueAccess access = new ValueAccess() {
                    @Override public Object get() throws ReflectiveOperationException {
                        return option.getClass().getMethod("get").invoke(option);
                    }

                    @Override public void set(@Nullable Object value) throws ReflectiveOperationException {
                        option.getClass().getMethod("set", Object.class).invoke(option, value);
                        markFirmamentDirty(config);
                    }
                };
                return new NativeSetting(configName + "." + property, label, kind, null, null, access);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }

        private List<NativeSetting> firmamentHudSettings(Object config, String configName, Object option) {
            try {
                Object value = option.getClass().getMethod("get").invoke(option);
                if (value == null || !value.getClass().getName().equals("moe.nea.firmament.gui.config.HudMeta")) {
                    return List.of();
                }
                String property = String.valueOf(option.getClass().getMethod("getPropertyName").invoke(option));
                return List.of(
                        firmamentHudAxis(config, configName, property, option, "x"),
                        firmamentHudAxis(config, configName, property, option, "y"),
                        firmamentHudScale(config, configName, property, option)
                );
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return List.of();
            }
        }

        private NativeSetting firmamentHudAxis(Object config, String configName, String property,
                                                Object option, String axis) {
            return new NativeSetting(configName + "." + property + "." + axis,
                    axis.toUpperCase(Locale.ROOT), ValueKind.INTEGER, -4096.0, 4096.0, new ValueAccess() {
                @Override public Object get() throws ReflectiveOperationException {
                    Object hud = option.getClass().getMethod("get").invoke(option);
                    Object position = hud.getClass().getMethod("getPosition").invoke(hud);
                    return position.getClass().getMethod(axis).invoke(position);
                }

                @Override public void set(@Nullable Object newValue) throws ReflectiveOperationException {
                    Object hud = option.getClass().getMethod("get").invoke(option);
                    Object position = hud.getClass().getMethod("getPosition").invoke(hud);
                    int x = ((Number) position.getClass().getMethod("x").invoke(position)).intValue();
                    int y = ((Number) position.getClass().getMethod("y").invoke(position)).intValue();
                    int changed = ((Number) newValue).intValue();
                    Method setter = hud.getClass().getMethod("setPosition", org.joml.Vector2ic.class);
                    setter.invoke(hud, axis.equals("x") ? new Vector2i(changed, y) : new Vector2i(x, changed));
                    markFirmamentDirty(config);
                }
            });
        }

        private NativeSetting firmamentHudScale(Object config, String configName, String property, Object option) {
            return new NativeSetting(configName + "." + property + ".scale", ModText.get("config.setting.scale"),
                    ValueKind.DECIMAL, 0.25, 4.0, new ValueAccess() {
                @Override public Object get() throws ReflectiveOperationException {
                    Object hud = option.getClass().getMethod("get").invoke(option);
                    return hud.getClass().getMethod("getScale").invoke(hud);
                }

                @Override public void set(@Nullable Object newValue) throws ReflectiveOperationException {
                    Object hud = option.getClass().getMethod("get").invoke(option);
                    hud.getClass().getMethod("setScale", float.class)
                            .invoke(hud, ((Number) newValue).floatValue());
                    markFirmamentDirty(config);
                }
            });
        }
    }

    private static void scanObject(ObjectGraphAdapter adapter, Object object, List<String> path,
                                   String fallbackName, int depth, Set<Object> visited,
                                   List<NativeFeature> result) throws ReflectiveOperationException {
        if (object == null || depth > MAX_SCAN_DEPTH || visited.contains(object)) return;
        visited.add(object);
        List<Member> members = members(object);
        Member primary = findPrimary(object, members);
        if (primary != null && !path.isEmpty()) {
            String title = memberLabel(primary.ownerMember(), humanize(fallbackName));
            String description = memberDescription(primary.ownerMember(),
                    adapter.provider.displayName + " · " + String.join(" / ", path));
            String fullPath = String.join(".", path);
            ConfigScreen.Category category = classify(fullPath);
            NativeSetting primarySetting = setting(adapter, append(path, primary.name()), primary, title);
            List<NativeSetting> settings = collectSettings(adapter, object, path, members, primary);
            result.add(new NativeFeature(adapter.provider, canonicalId(category, title), title, description,
                    category, groupName(fullPath, fallbackName), primarySetting, settings));
            return;
        }
        for (Member member : members) {
            if (member.simple()) {
                if ((member.kind() == ValueKind.BOOLEAN || member.kind() == ValueKind.ENUM) && !path.isEmpty()) {
                    String title = memberLabel(member.ownerMember(), humanize(member.name()));
                    String description = memberDescription(member.ownerMember(), adapter.provider.displayName
                            + " · " + String.join(" / ", path));
                    String fullPath = String.join(".", append(path, member.name()));
                    ConfigScreen.Category category = classify(fullPath);
                    NativeSetting primarySetting = setting(adapter, append(path, member.name()), member, title);
                    result.add(new NativeFeature(adapter.provider, canonicalId(category, title), title, description,
                            category, groupName(fullPath, path.getLast()), primarySetting, List.of()));
                }
                continue;
            }
            Object child;
            try {
                child = member.read(object);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                continue;
            }
            if (child == null || !belongsToProvider(adapter.provider, child.getClass())) continue;
            scanObject(adapter, child, append(path, member.name()), member.name(), depth + 1, visited, result);
        }
    }

    private static List<NativeSetting> collectSettings(ObjectGraphAdapter adapter, Object object,
                                                       List<String> path, List<Member> members, Member primary) {
        List<NativeSetting> result = new ArrayList<>();
        for (Member member : members) {
            if (member == primary || result.size() >= MAX_SETTINGS_PER_FEATURE) continue;
            if (member.simple()) {
                result.add(setting(adapter, append(path, member.name()), member,
                        memberLabel(member.ownerMember(), humanize(member.name()))));
                continue;
            }
            if (adapter.provider == Provider.SKYHANNI
                    && member.ownerMember().getType().getName()
                    .equals("at.hannibal2.skyhanni.config.core.config.Position")) {
                result.addAll(skyHanniPositionSettings(adapter, append(path, member.name())));
            }
        }
        return result;
    }

    private static List<NativeSetting> skyHanniPositionSettings(ObjectGraphAdapter adapter, List<String> path) {
        return List.of(
                skyHanniPositionAxis(adapter, path, "x"),
                skyHanniPositionAxis(adapter, path, "y"),
                new NativeSetting(String.join(".", append(path, "scale")), ModText.get("config.setting.scale"),
                        ValueKind.DECIMAL, 0.1, 10.0, new ValueAccess() {
                    @Override public Object get() throws ReflectiveOperationException {
                        Object position = readRawPath(adapter.root(), path);
                        return position.getClass().getMethod("getScale").invoke(position);
                    }

                    @Override public void set(@Nullable Object value) throws ReflectiveOperationException {
                        Object root = adapter.root();
                        Object position = readRawPath(root, path);
                        position.getClass().getMethod("setScale", float.class)
                                .invoke(position, ((Number) value).floatValue());
                        adapter.save(root);
                    }
                })
        );
    }

    private static NativeSetting skyHanniPositionAxis(ObjectGraphAdapter adapter, List<String> path, String axis) {
        return new NativeSetting(String.join(".", append(path, axis)), axis.toUpperCase(Locale.ROOT),
                ValueKind.INTEGER, -4096.0, 4096.0, new ValueAccess() {
            @Override public Object get() throws ReflectiveOperationException {
                Object position = readRawPath(adapter.root(), path);
                return position.getClass().getMethod("get" + axis.toUpperCase(Locale.ROOT)).invoke(position);
            }

            @Override public void set(@Nullable Object value) throws ReflectiveOperationException {
                Object root = adapter.root();
                Object position = readRawPath(root, path);
                int currentX = ((Number) position.getClass().getMethod("getX").invoke(position)).intValue();
                int currentY = ((Number) position.getClass().getMethod("getY").invoke(position)).intValue();
                int changed = ((Number) value).intValue();
                position.getClass().getMethod("moveTo", int.class, int.class)
                        .invoke(position, axis.equals("x") ? changed : currentX,
                                axis.equals("y") ? changed : currentY);
                adapter.save(root);
            }
        });
    }

    private static NativeSetting setting(ObjectGraphAdapter adapter, List<String> path,
                                         Member member, String label) {
        double[] range = sliderRange(member.ownerMember());
        if (range == null && member.kind() == ValueKind.INTEGER
                && (member.name().equalsIgnoreCase("x") || member.name().equalsIgnoreCase("y"))) {
            range = new double[]{-4096.0, 4096.0};
        } else if (range == null && member.kind() == ValueKind.DECIMAL
                && member.name().equalsIgnoreCase("scale")) {
            range = new double[]{0.25, 4.0};
        }
        return new NativeSetting(String.join(".", path), label, member.kind(),
                range == null ? null : range[0], range == null ? null : range[1], new ValueAccess() {
            @Override public Object get() throws ReflectiveOperationException {
                return unwrap(adapter.read(path));
            }

            @Override public void set(@Nullable Object value) throws ReflectiveOperationException {
                adapter.write(path, value);
            }
        });
    }

    private static @Nullable Member findPrimary(Object object, List<Member> members) {
        for (String preferred : List.of("enabled", "enable", "isEnabled", "visible", "active")) {
            for (Member member : members) {
                if (member.name().equalsIgnoreCase(preferred) && (member.kind() == ValueKind.BOOLEAN
                        || member.kind() == ValueKind.ENUM)) return member;
            }
        }
        return null;
    }

    private static List<Member> members(Object owner) {
        Class<?> type = owner.getClass();
        Map<String, Field> fields = new LinkedHashMap<>();
        for (Class<?> cursor = type; cursor != null && cursor != Object.class; cursor = cursor.getSuperclass()) {
            for (Field field : cursor.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || field.getName().contains("$")) continue;
                fields.putIfAbsent(field.getName(), field);
            }
        }
        List<Member> result = new ArrayList<>();
        for (Field field : fields.values()) {
            field.setAccessible(true);
            Class<?> valueType = field.getType();
            ValueKind kind = valueKind(valueType);
            boolean property = isProperty(valueType);
            if (property) {
                try {
                    Object wrapper = field.get(owner);
                    Object actual = wrapper == null ? null : wrapper.getClass().getMethod("get").invoke(wrapper);
                    kind = actual == null ? ValueKind.UNSUPPORTED : valueKind(actual.getClass());
                } catch (ReflectiveOperationException | RuntimeException ignored) { }
            }
            result.add(new Member(field.getName(), field, kind, kind != ValueKind.UNSUPPORTED, property));
        }
        return result;
    }

    private record Member(String name, Field ownerMember, ValueKind kind, boolean simple, boolean property) {
        Object read(Object owner) throws ReflectiveOperationException {
            Object value = ownerMember.get(owner);
            return property && value != null ? value.getClass().getMethod("get").invoke(value) : value;
        }
    }

    private interface ValueAccess {
        @Nullable Object get() throws ReflectiveOperationException;
        void set(@Nullable Object value) throws ReflectiveOperationException;
    }

    private static Object readPath(Object root, List<String> path) throws ReflectiveOperationException {
        Object current = readRawPath(root, path);
        return unwrap(current);
    }

    private static Object readRawPath(Object root, List<String> path) throws ReflectiveOperationException {
        Object current = root;
        for (String segment : path) {
            Field field = findField(current.getClass(), segment);
            field.setAccessible(true);
            current = field.get(current);
            if (current == null) return null;
        }
        return current;
    }

    private static void writePath(Object root, List<String> path, @Nullable Object value)
            throws ReflectiveOperationException {
        Object current = root;
        for (int index = 0; index < path.size() - 1; index++) {
            Field field = findField(current.getClass(), path.get(index));
            field.setAccessible(true);
            current = field.get(current);
            if (current == null) throw new IllegalStateException("Null config path");
        }
        Field field = findField(current.getClass(), path.getLast());
        field.setAccessible(true);
        Object existing = field.get(current);
        if (isProperty(field.getType()) && existing != null) {
            Method set = findSingleArgumentMethod(existing.getClass(), "set");
            set.invoke(existing, coerce(value, set.getParameterTypes()[0]));
        } else {
            field.set(current, coerce(value, field.getType()));
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        for (Class<?> cursor = type; cursor != null; cursor = cursor.getSuperclass()) {
            try {
                return cursor.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) { }
        }
        throw new NoSuchFieldException(type.getName() + "." + name);
    }

    private static Method findSingleArgumentMethod(Class<?> type, String name) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 1) return method;
        }
        throw new NoSuchMethodException(type.getName() + "." + name);
    }

    private static Object unwrap(Object value) throws ReflectiveOperationException {
        if (value != null && isProperty(value.getClass())) return value.getClass().getMethod("get").invoke(value);
        return value;
    }

    private static boolean isProperty(Class<?> type) {
        String name = type.getName();
        return name.endsWith(".Property") || name.contains("moulconfig.observer.Property");
    }

    private static @Nullable Object coerce(@Nullable Object value, Class<?> target) {
        if (value == null) return null;
        if (target.isInstance(value)) return value;
        if (value instanceof Number number) {
            if (target == int.class || target == Integer.class) return number.intValue();
            if (target == long.class || target == Long.class) return number.longValue();
            if (target == float.class || target == Float.class) return number.floatValue();
            if (target == double.class || target == Double.class) return number.doubleValue();
        }
        return value;
    }

    private static ValueKind valueKind(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) return ValueKind.BOOLEAN;
        if (type == byte.class || type == short.class || type == int.class || type == long.class
                || type == Byte.class || type == Short.class || type == Integer.class || type == Long.class) {
            return ValueKind.INTEGER;
        }
        if (type == float.class || type == double.class || type == Float.class || type == Double.class) {
            return ValueKind.DECIMAL;
        }
        if (type.isEnum()) return ValueKind.ENUM;
        if (type == String.class) return ValueKind.STRING;
        return ValueKind.UNSUPPORTED;
    }

    private static boolean belongsToProvider(Provider provider, Class<?> type) {
        String name = type.getName();
        return switch (provider) {
            case SKYHANNI -> name.startsWith("at.hannibal2.skyhanni.config");
            case SKYBLOCKER -> name.startsWith("de.hysky.skyblocker.config");
            case BABYZOMBIE -> name.startsWith("top.babyzombie.addons.config");
            default -> false;
        };
    }

    private static ConfigScreen.Category classify(String path) {
        String normalized = path.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "dungeon", "catacomb", "terminal", "secret")) return ConfigScreen.Category.DUNGEONS;
        if (normalized.contains("slayer")) return ConfigScreen.Category.SLAYER;
        if (containsAny(normalized, "farming", "garden", "pest", "visitor", "crop")) return ConfigScreen.Category.FARMING;
        if (containsAny(normalized, "foraging", "galatea", "torrhus", "tree", "sweep")) return ConfigScreen.Category.FORAGING;
        if (containsAny(normalized, "fishing", "fish", "sea creature", "bobber", "lava fishing")) return ConfigScreen.Category.FISHING;
        if (containsAny(normalized, "hunting", "safari", "lasso", "critter", "shard")) return ConfigScreen.Category.HUNTING;
        if (containsAny(normalized, "mining", "dwarven", "crystal hollow", "glacite", "powder", "commission")) return ConfigScreen.Category.MINING;
        if (normalized.contains("rift")) return ConfigScreen.Category.RIFT;
        if (containsAny(normalized, "event", "carnival", "raffle", "anniversary", "spooky")) return ConfigScreen.Category.EVENTS;
        if (containsAny(normalized, "map", "waypoint", "fairy soul")) return ConfigScreen.Category.MAPS;
        if (containsAny(normalized, "inventory", "item", "storage", "tooltip", "pet", "menu", "wardrobe", "bazaar")) {
            return ConfigScreen.Category.ITEMS_AND_MENUS;
        }
        if (containsAny(normalized, "combat", "crimson", "kuudra", "dragon", "dojo", "mob")) {
            return ConfigScreen.Category.COMBAT;
        }
        return ConfigScreen.Category.GENERAL;
    }

    private static String groupName(String path, String fallback) {
        String normalized = path.toLowerCase(Locale.ROOT);
        if (normalized.contains("safari")) return "Safari";
        if (normalized.contains("crimson")) return "Crimson Isle";
        if (normalized.contains("kuudra")) return "Kuudra";
        if (normalized.contains("garden")) return "Garden";
        if (normalized.contains("rift")) return "Rift";
        if (normalized.contains("dungeon")) return "Dungeons";
        return humanize(fallback);
    }

    private static String canonicalId(ConfigScreen.Category category, String title) {
        String normalized = title.toLowerCase(Locale.ROOT).replaceAll("§[0-9a-fk-or]", "")
                .replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return alias(category.name().toLowerCase(Locale.ROOT) + ":" + normalized);
    }

    private static String alias(String id) {
        Map<String, String> aliases = Map.ofEntries(
                Map.entry("items_and_menus:pet_hud", "items_and_menus:pet_display"),
                Map.entry("items_and_menus:pet_overlay", "items_and_menus:pet_display"),
                Map.entry("items_and_menus:pet_display", "items_and_menus:pet_display"),
                Map.entry("maps:fairy_soul_waypoints", "maps:fairy_souls"),
                Map.entry("maps:fairy_souls", "maps:fairy_souls"),
                Map.entry("hunting:lasso_display", "hunting:lasso_hud"),
                Map.entry("hunting:lasso_hud", "hunting:lasso_hud"),
                Map.entry("general:save_cursor_position", "items_and_menus:cursor_memory"),
                Map.entry("items_and_menus:save_cursor_position", "items_and_menus:cursor_memory"),
                Map.entry("items_and_menus:cursor_memory", "items_and_menus:cursor_memory"),
                Map.entry("items_and_menus:lore_timers", "items_and_menus:item_timestamps"),
                Map.entry("items_and_menus:item_timestamps", "items_and_menus:item_timestamps")
        );
        return aliases.getOrDefault(id, id);
    }

    private static boolean containsAny(String value, String... needles) {
        for (String needle : needles) if (value.contains(needle)) return true;
        return false;
    }

    private static List<String> append(List<String> path, String value) {
        List<String> result = new ArrayList<>(path);
        result.add(value);
        return List.copyOf(result);
    }

    private static String humanize(String value) {
        if (value == null || value.isBlank()) return "Feature";
        String spaced = value.replace('_', ' ').replace('-', ' ')
                .replaceAll("([a-z0-9])([A-Z])", "$1 $2").replaceAll("\\s+", " ").trim();
        StringBuilder result = new StringBuilder();
        for (String word : spaced.split(" ")) {
            if (!result.isEmpty()) result.append(' ');
            if (word.length() <= 3 && word.equals(word.toUpperCase(Locale.ROOT))) result.append(word);
            else result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    private static String memberLabel(Field field, String fallback) {
        return annotationText(field, "ConfigOption", "name", fallback);
    }

    private static String memberDescription(Field field, String fallback) {
        return annotationText(field, "ConfigOption", "desc", fallback);
    }

    private static String annotationText(Field field, String annotationName, String property, String fallback) {
        for (Annotation annotation : field.getAnnotations()) {
            if (!annotation.annotationType().getSimpleName().equals(annotationName)) continue;
            try {
                String value = String.valueOf(annotation.annotationType().getMethod(property).invoke(annotation));
                if (!value.isBlank()) {
                    String translated = Component.translatable(value).getString();
                    return translated.equals(value) ? humanize(value) : translated;
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) { }
        }
        return fallback;
    }

    private static @Nullable double[] sliderRange(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (!annotation.annotationType().getSimpleName().contains("Slider")) continue;
            try {
                double minimum = ((Number) annotation.annotationType().getMethod("minValue").invoke(annotation)).doubleValue();
                double maximum = ((Number) annotation.annotationType().getMethod("maxValue").invoke(annotation)).doubleValue();
                if (maximum > minimum) return new double[]{minimum, maximum};
            } catch (ReflectiveOperationException | RuntimeException ignored) { }
        }
        return null;
    }

    private static String componentString(Object owner, String getter, String fallback) {
        try {
            Object component = owner.getClass().getMethod(getter).invoke(owner);
            if (component instanceof Component value) {
                String text = value.getString();
                if (!text.isBlank() && !text.contains("firmament.config.")) return text;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) { }
        return fallback;
    }

    private static void markFirmamentDirty(Object config) throws ReflectiveOperationException {
        Method method = config.getClass().getMethod("markDirty", java.util.concurrent.CompletableFuture.class);
        method.invoke(config, new Object[]{null});
    }

    private static final class MutableUnified {
        final String id;
        @Nullable String title;
        @Nullable String description;
        ConfigScreen.@Nullable Category category;
        @Nullable String group;
        ConfigScreen.@Nullable Feature local;
        final List<NativeFeature> external = new ArrayList<>();

        MutableUnified(String id) {
            this.id = id;
        }
    }
}
