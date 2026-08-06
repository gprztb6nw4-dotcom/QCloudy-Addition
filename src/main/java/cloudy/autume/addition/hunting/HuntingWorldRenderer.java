package cloudy.autume.addition.hunting;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.tracker.IslandArea;
import cloudy.autume.addition.tracker.LocationTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

/** World markers built only from fixed Wiki coordinates or client-visible world state. */
public final class HuntingWorldRenderer {
    private static final int CAMPFIRE_COLOR = 0xFFFF3030;
    private static final int FAIRY_SOUL_COLOR = 0xFFFF69B4;
    private static final int WUMPA_ROUTE_COLOR = 0xE6FF3030;
    private static final int BEEHEEMOTH_BEACON_COLOR = 0xFFFFD700;

    static final List<BlockPos> TORRHUS_FAIRY_SOULS = List.of(
            new BlockPos(-618, 125, 179), new BlockPos(-603, 167, 234),
            new BlockPos(-706, 98, 142), new BlockPos(-565, 136, 243),
            new BlockPos(-640, 90, 158), new BlockPos(-677, 102, 262),
            new BlockPos(-627, 122, 285), new BlockPos(-567, 81, 272),
            new BlockPos(-554, 94, 256), new BlockPos(-678, 62, 258),
            new BlockPos(-711, 59, 290), new BlockPos(-727, 125, 152));

    static final List<BlockPos> SAFARI_FAIRY_SOULS = List.of(
            new BlockPos(5, 106, 18), new BlockPos(-162, 60, 63),
            new BlockPos(-75, 40, -32), new BlockPos(40, 63, -14));

    private HuntingWorldRenderer() {
    }

    public static void register() {
        LevelRenderEvents.COLLECT_SUBMITS.register(HuntingWorldRenderer::render);
    }

    private static void render(LevelRenderContext context) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null || !LocationTracker.isSkyBlock()) return;

        BlockPos campfire = HuntingTracker.nearestCampfire();
        if (campfire != null && HuntingTracker.campfireBeaconActive()) {
            submitBeacon(context, campfire, CAMPFIRE_COLOR);
        }

        BlockPos beeheemoth = HuntingTracker.beeheemothBeacon();
        if (LocationTracker.area() == IslandArea.TORRHUS_CANYON && beeheemoth != null) {
            submitBeacon(context, beeheemoth, BEEHEEMOTH_BEACON_COLOR);
        }

        var config = ConfigManager.get().hunting;
        if (config.fairySoulWaypoints) {
            if (LocationTracker.area() == IslandArea.TORRHUS_CANYON && config.fairySoulTorrhus) {
                for (BlockPos pos : TORRHUS_FAIRY_SOULS) {
                    if (!HuntingTracker.fairySoulFound(IslandArea.TORRHUS_CANYON, pos)) {
                        submitBeacon(context, pos, FAIRY_SOUL_COLOR);
                    }
                }
            } else if (LocationTracker.area() == IslandArea.CRITTER_SAFARI && config.fairySoulSafari) {
                for (BlockPos pos : SAFARI_FAIRY_SOULS) {
                    if (!HuntingTracker.fairySoulFound(IslandArea.CRITTER_SAFARI, pos)) {
                        submitBeacon(context, pos, FAIRY_SOUL_COLOR);
                    }
                }
            }
        }

        HuntingTracker.Route route = HuntingTracker.wumpaRoute();
        if (route != null && LocationTracker.area() == IslandArea.CRITTER_SAFARI) {
            submitLine(context, route.start(), route.end());
        }

        if (LocationTracker.area() == IslandArea.CRITTER_SAFARI && config.snoozleWallOverlay) {
            submitWallOverlay(context, HuntingTracker.snoozleWallFaces(), config.snoozleWallOverlayColor);
        }
    }

    static List<BlockPos> fairySouls(IslandArea area) {
        return area == IslandArea.TORRHUS_CANYON ? TORRHUS_FAIRY_SOULS
                : area == IslandArea.CRITTER_SAFARI ? SAFARI_FAIRY_SOULS : List.of();
    }

    private static void submitBeacon(LevelRenderContext context, BlockPos pos, int color) {
        Minecraft client = Minecraft.getInstance();
        Vec3 camera = context.levelState().cameraRenderState.pos;
        float animationTime = Math.floorMod(client.level.getGameTime(), 40)
                + client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        PoseStack pose = context.poseStack();
        pose.pushPose();
        pose.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        BeaconRenderer.submitBeaconBeam(pose, context.submitNodeCollector(), BeaconRenderer.BEAM_LOCATION,
                1.0f, animationTime, 0, 319, color, 0.2f, 0.25f);
        pose.popPose();
    }

    private static void submitLine(LevelRenderContext context, Vec3 start, Vec3 end) {
        Vec3 camera = context.levelState().cameraRenderState.pos;
        Vec3 delta = end.subtract(start);
        if (delta.lengthSqr() < 1.0E-5) return;
        Vector3f normal = delta.toVector3f().normalize();
        PoseStack pose = context.poseStack();
        pose.pushPose();
        pose.translate(start.x - camera.x, start.y - camera.y, start.z - camera.z);
        context.submitNodeCollector().submitCustomGeometry(pose, RenderTypes.LINES_TRANSLUCENT,
                (entry, buffer) -> {
                    buffer.addVertex(entry, 0.0f, 0.0f, 0.0f)
                            .setColor(WUMPA_ROUTE_COLOR)
                            .setNormal(entry, normal.x(), normal.y(), normal.z())
                            .setLineWidth(4.0f);
                    buffer.addVertex(entry, (float) delta.x, (float) delta.y, (float) delta.z)
                            .setColor(WUMPA_ROUTE_COLOR)
                            .setNormal(entry, normal.x(), normal.y(), normal.z())
                            .setLineWidth(4.0f);
                });
        pose.popPose();
    }

    private static void submitWallOverlay(LevelRenderContext context, List<HuntingTracker.WallFace> faces, int rgb) {
        if (faces.isEmpty()) return;
        Vec3 camera = context.levelState().cameraRenderState.pos;
        int color = 0x66000000 | (rgb & 0xFFFFFF);
        PoseStack pose = context.poseStack();
        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);
        context.submitNodeCollector().submitCustomGeometry(pose, RenderTypes.debugQuads(),
                (entry, buffer) -> faces.forEach(face -> submitWallFace(entry, buffer, face, color)));
        pose.popPose();
    }

    private static void submitWallFace(PoseStack.Pose pose, VertexConsumer buffer,
                                       HuntingTracker.WallFace wallFace, int color) {
        float x0 = wallFace.pos().getX();
        float y0 = wallFace.pos().getY();
        float z0 = wallFace.pos().getZ();
        float x1 = x0 + 1.0f;
        float y1 = y0 + 1.0f;
        float z1 = z0 + 1.0f;
        float epsilon = 0.002f;
        switch (wallFace.face()) {
            case DOWN -> addQuad(pose, buffer, color,
                    x0, y0 - epsilon, z0, x1, y0 - epsilon, z0,
                    x1, y0 - epsilon, z1, x0, y0 - epsilon, z1);
            case UP -> addQuad(pose, buffer, color,
                    x0, y1 + epsilon, z1, x1, y1 + epsilon, z1,
                    x1, y1 + epsilon, z0, x0, y1 + epsilon, z0);
            case NORTH -> addQuad(pose, buffer, color,
                    x1, y0, z0 - epsilon, x0, y0, z0 - epsilon,
                    x0, y1, z0 - epsilon, x1, y1, z0 - epsilon);
            case SOUTH -> addQuad(pose, buffer, color,
                    x0, y0, z1 + epsilon, x1, y0, z1 + epsilon,
                    x1, y1, z1 + epsilon, x0, y1, z1 + epsilon);
            case WEST -> addQuad(pose, buffer, color,
                    x0 - epsilon, y0, z0, x0 - epsilon, y0, z1,
                    x0 - epsilon, y1, z1, x0 - epsilon, y1, z0);
            case EAST -> addQuad(pose, buffer, color,
                    x1 + epsilon, y0, z1, x1 + epsilon, y0, z0,
                    x1 + epsilon, y1, z0, x1 + epsilon, y1, z1);
        }
    }

    private static void addQuad(PoseStack.Pose pose, VertexConsumer buffer, int color,
                                float ax, float ay, float az, float bx, float by, float bz,
                                float cx, float cy, float cz, float dx, float dy, float dz) {
        buffer.addVertex(pose, ax, ay, az).setColor(color);
        buffer.addVertex(pose, bx, by, bz).setColor(color);
        buffer.addVertex(pose, cx, cy, cz).setColor(color);
        buffer.addVertex(pose, dx, dy, dz).setColor(color);
    }
}
