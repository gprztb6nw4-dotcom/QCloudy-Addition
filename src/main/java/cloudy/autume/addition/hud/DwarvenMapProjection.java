package cloudy.autume.addition.hud;

/**
 * Projects the local player's X/Z position onto the bundled one-layer Dwarven overview.
 *
 * <p>The artwork is a schematic background rather than a survey map, so this deliberately uses
 * one continuous, approximate transform for the whole island. Y and the scoreboard sub-location
 * are not accepted by this API: walking above or below another area, including the bridges over
 * The Mist, therefore cannot move the marker into a different region. The same X/Z pair always
 * produces the same map point.</p>
 */
final class DwarvenMapProjection {
    private static final double WORLD_MIN_X = -159.0;
    private static final double WORLD_MAX_X = 181.0;
    private static final double WORLD_MIN_Z = -154.0;
    private static final double WORLD_MAX_Z = 203.0;

    // Insets keep the complete 12x12 arrow inside the 200x200 background at the world extremes.
    private static final float MAP_LEFT = 18.0f;
    private static final float MAP_RIGHT = 181.0f;
    private static final float MAP_TOP = 7.0f;
    private static final float MAP_BOTTOM = 161.0f;

    private DwarvenMapProjection() {
    }

    static Point project(double x, double z) {
        return new Point(projectAxis(x, WORLD_MIN_X, WORLD_MAX_X, MAP_LEFT, MAP_RIGHT),
                projectAxis(z, WORLD_MIN_Z, WORLD_MAX_Z, MAP_TOP, MAP_BOTTOM));
    }

    private static float projectAxis(double value, double worldMinimum, double worldMaximum,
                                     float mapMinimum, float mapMaximum) {
        double normalized = Math.clamp((value - worldMinimum) / (worldMaximum - worldMinimum),
                0.0, 1.0);
        return (float) (mapMinimum + normalized * (mapMaximum - mapMinimum));
    }

    record Point(float x, float y) {
    }
}
