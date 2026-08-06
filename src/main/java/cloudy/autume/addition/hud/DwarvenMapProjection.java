package cloudy.autume.addition.hud;

import java.util.Locale;

/**
 * Projects client-visible Dwarven coordinates onto the simplified one-layer map.
 * The scoreboard sub-location selects the correct schematic region; the player's
 * X/Y/Z coordinates then place the marker inside that region. No server query or
 * packet is involved.
 */
final class DwarvenMapProjection {
    private static final Region[] REGIONS = {
            new Region(new String[]{"royal palace", "palace bridge", "royal quarters", "barracks of heroes",
                    "grand library", "hanging court", "aristocrat passage"},
                    99, 159, 115, 203, 184, 225, 145, 176, 145, 185),
            new Region(new String[]{"dwarven village", "dwarven tavern", "the lift", "gates to the mines"},
                    -45, 64, -154, -68, 198, 230, 76, 133, 15, 45),
            new Region(new String[]{"upper mines"},
                    -155, -63, -84, -8, 160, 225, 24, 67, 34, 65),
            new Region(new String[]{"rampart's quarry", "rampart quarry"},
                    -124, -27, -70, 30, 128, 225, 31, 84, 61, 95),
            new Region(new String[]{"forge basin", "the forge"},
                    -23, 26, -32, 24, 140, 175, 89, 119, 55, 80),
            new Region(new String[]{"lava springs"},
                    8, 82, -20, 6, 185, 202, 124, 149, 57, 78),
            new Region(new String[]{"cliffside veins"},
                    -33, 97, 7, 60, 124, 155, 79, 145, 90, 108),
            new Region(new String[]{"far reserve", "miner's guild", "ironman's guild"},
                    -157, -95, 5, 131, 138, 180, 17, 64, 99, 132),
            new Region(new String[]{"goblin burrows"},
                    -159, -57, 91, 155, 130, 170, 17, 76, 134, 165),
            new Region(new String[]{"the mist"},
                    -19, 128, 54, 129, 78, 98, 78, 151, 116, 148),
            new Region(new String[]{"great ice wall"},
                    -29, 84, 130, 167, 124, 170, 78, 147, 153, 173),
            new Region(new String[]{"royal mines", "divan's gateway"},
                    92, 156, 29, 154, 98, 175, 155, 183, 79, 120),
    };

    private DwarvenMapProjection() {
    }

    static Point project(double x, double y, double z, String visibleLocation) {
        String location = visibleLocation == null ? "" : visibleLocation.toLowerCase(Locale.ROOT);
        for (Region region : REGIONS) {
            if (region.matches(location)) return region.project(x, z);
        }

        Region nearest = REGIONS[0];
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Region region : REGIONS) {
            double distance = region.distance(x, y, z);
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
                          double minY, double maxY,
                          float mapMinX, float mapMaxX, float mapMinY, float mapMaxY) {
        boolean matches(String location) {
            for (String alias : aliases) if (location.contains(alias)) return true;
            return false;
        }

        Point project(double x, double z) {
            return new Point(map(x, minX, maxX, mapMinX, mapMaxX),
                    map(z, minZ, maxZ, mapMinY, mapMaxY));
        }

        double distance(double x, double y, double z) {
            double xDistance = outsideDistance(x, minX, maxX) / Math.max(1.0, maxX - minX);
            double yDistance = outsideDistance(y, minY, maxY) / Math.max(1.0, maxY - minY);
            double zDistance = outsideDistance(z, minZ, maxZ) / Math.max(1.0, maxZ - minZ);
            return xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;
        }

        private static double outsideDistance(double value, double minimum, double maximum) {
            if (value < minimum) return minimum - value;
            if (value > maximum) return value - maximum;
            return 0.0;
        }

        private static float map(double value, double minimum, double maximum, float mapMin, float mapMax) {
            double normalized = Math.clamp((value - minimum) / (maximum - minimum), 0.0, 1.0);
            return (float) (mapMin + normalized * (mapMax - mapMin));
        }
    }
}
