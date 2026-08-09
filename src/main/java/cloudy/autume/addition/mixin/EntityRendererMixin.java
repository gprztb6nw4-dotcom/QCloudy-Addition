package cloudy.autume.addition.mixin;

import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.hunting.HuntingTracker;
import cloudy.autume.addition.tracker.IslandArea;
import cloudy.autume.addition.tracker.LocationTracker;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(value = EntityRenderer.class, priority = 900)
public class EntityRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void autumeCloudyAddition$highlightDragon(Entity entity, EntityRenderState state,
                                                       float tickDelta, CallbackInfo callbackInfo) {
        if (ConfigManager.get().combat.enderDragonHighlight
                && LocationTracker.area() == IslandArea.THE_END
                && entity instanceof EnderDragon) {
            state.outlineColor = ARGB.opaque(ConfigManager.get().combat.enderDragonHighlightColor);
        }
        if (ConfigManager.get().hunting.beeheemothHelper
                && ConfigManager.get().hunting.beeheemothOutline
                && LocationTracker.area() == IslandArea.TORRHUS_CANYON
                && HuntingTracker.isBeeheemoth(entity)) {
            state.outlineColor = ARGB.opaque(ConfigManager.get().hunting.beeheemothOutlineColor);
        }
        if (ConfigManager.get().hunting.sparklingAlert
                && ConfigManager.get().hunting.sparklingOutline
                && LocationTracker.area() == IslandArea.CRITTER_SAFARI
                && entity.hasCustomName() && entity.getCustomName() != null
                && entity.getCustomName().getString().toUpperCase(Locale.ROOT).contains("SPARKLING")) {
            if (!(entity instanceof ArmorStand)) {
                state.outlineColor = ARGB.opaque(ConfigManager.get().hunting.sparklingOutlineColor);
            }
        }
    }
}
