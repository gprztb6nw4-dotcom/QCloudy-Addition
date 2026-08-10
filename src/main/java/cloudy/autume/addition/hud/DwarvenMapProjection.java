package cloudy.autume.addition.hud;

import java.util.Locale;

/**
 * Projects client-visible Dwarven coordinates onto the bundled one-layer map.
 * The scoreboard sub-location selects the correct schematic region; the player's
 * X/Z coordinates then place the marker inside that region. The map is deliberately
 * single-layer, so Y never participates in region selection or marker placement. No server query or
 * packet is involved.
 */
final class DwarvenMapProjection {
    private static final Region[] REGIONS = {
            new Region(new String[]{"royal palace", "palace bridge", "royal quarters", "barracks of heroes",
                    "grand library", "hanging court", "aristocrat passage"},
                    99, 159, 115, 203,
                    new MapQuad(141, 143, 181, 143, 147, 163, 177, 163)),
            new Region(new String[]{"dwarven village", "dwarven tavern", "the lift", "gates to the mines"},
                    -45, 64, -154, -68,
                    new MapQuad(82, 9, 126, 9, 82, 29, 126, 29)),
            new Region(new String[]{"upper mines"},
                    -155, -63, -84, -8,
                    new MapQuad(28, 36, 55, 36, 28, 55, 55, 55)),
            new Region(new String[]{"rampart's quarry", "rampart quarry"},
                    -124, -27, -70, 30,
                    new MapQuad(48, 50, 77, 48, 47, 83, 75, 84)),
            new Region(new String[]{"forge basin", "the forge"},
                    -23, 26, -32, 24,
                    new MapQuad(92, 62, 105, 62, 92, 73, 105, 73)),
            new Region(new String[]{"lava springs"},
                    8, 82, -20, 6,
                    new MapQuad(120, 64, 133, 64, 120, 74, 133, 74)),
            new Region(new String[]{"cliffside veins"},
                    -33, 97, 7, 60,
                    new MapQuad(94, 86, 136, 86, 94, 96, 136, 96)),
            new Region(new String[]{"far reserve", "miner's guild", "ironman's guild"},
                    -157, -95, 5, 131,
                    new MapQuad(23, 74, 35, 74, 23, 125, 35, 125)),
            new Region(new String[]{"goblin burrows"},
                    -159, -57, 91, 155,
                    new MapQuad(31, 128, 62, 128, 31, 139, 62, 139)),
            new Region(new String[]{"the mist"},
                    -19, 128, 54, 129,
                    new MapQuad(71, 106, 141, 106, 71, 130, 141, 130)),
            new Region(new String[]{"great ice wall"},
                    -29, 84, 130, 167,
                    new MapQuad(79, 138, 122, 138, 80, 148, 121, 148)),
            new Region(new String[]{"royal mines", "divan's gateway"},
                    92, 156, 29, 154,
                    new MapQuad(152, 88, 176, 88, 152, 113, 173, 113)),
    };

    private DwarvenMapProjection() {
    }

    static Point project(double x, double z, String visibleLocation) {
        String location = visibleLocation == null ? "" : visibleLocation.toLowerCase(Locale.ROOT);
        for (Region region : REGIONS) {
            if (region.matches(location)) return region.project(x, z);
        }

        Region nearest = REGIONS[0];
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Region region : REGIONS) {
            double distance = region.distance(x, z);
            if (distance < nearestDistance) {
                nearest = region;
                nearestDistance = distance;
            }
        }
        return nearest.project(x, z);
    }

    record Point(float x, float y) {
    }

    private record Region(String[] aliases,
                          double minX, double maxX, double minZ, double maxZ,
                          MapQuad mapQuad) {
        boolean matches(String location) {
            for (String alias : aliases) if (location.contains(alias)) return true;
            return false;
        }

        Point project(double x, double z) {
            double normalizedX = normalize(x, minX, maxX);
            double normalizedZ = normalize(z, minZ, maxZ);
            return mapQuad.project(normalizedX, normalizedZ);
        }

        double distance(double x, double z) {
            double centerX = (minX + maxX) * 0.5;
            double centerZ = (minZ + maxZ) * 0.5;
            double xDistance = (x - centerX) / Math.max(1.0, maxX - minX);
            double zDistance = (z - centerZ) / Math.max(1.0, maxZ - minZ);
            return xDistance * xDistance + zDistance * zDistance;
        }

        private static double normalize(double value, double minimum, double maximum) {
            return Math.clamp((value - minimum) / (maximum - minimum), 0.0, 1.0);
        }
    }

    /**
     * A convex, inset calibration area inside one drawn map region. Bilinear interpolation keeps
     * X/Z movement continuous while following slanted schematic regions instead of treating every
     * location as an unrelated axis-aligned box.
     */
    private record MapQuad(float topLeftX, float topLeftY,
                           float topRightX, float topRightY,
                           float bottomLeftX, float bottomLeftY,
                           float bottomRightX, float bottomRightY) {
        Point project(double normalizedX, double normalizedZ) {
            float topX = lerp(topLeftX, topRightX, normalizedX);
            float bottomX = lerp(bottomLeftX, bottomRightX, normalizedX);
            float topY = lerp(topLeftY, topRightY, normalizedX);
            float bottomY = lerp(bottomLeftY, bottomRightY, normalizedX);
            return new Point(lerp(topX, bottomX, normalizedZ),
                    lerp(topY, bottomY, normalizedZ));
        }

        private static float lerp(float start, float end, double amount) {
            return (float) (start + (end - start) * amount);
        }
    }
}
