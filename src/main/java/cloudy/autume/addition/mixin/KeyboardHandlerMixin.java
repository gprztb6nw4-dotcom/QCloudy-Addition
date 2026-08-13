package cloudy.autume.addition.mixin;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.compat.MinecraftClientCompat;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.config.ConfigScreen;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyboardHandler.class, priority = 980)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void aca$openConfigChord(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (action != org.lwjgl.glfw.GLFW.GLFW_PRESS || MinecraftClientCompat.screen(minecraft) != null) return;
        if (QCloudyAdditionClient.matchesChord(
                QCloudyAdditionClient.ChordAction.OPEN_CONFIG, event)) {
            MinecraftClientCompat.setScreen(minecraft, new ConfigScreen(null));
            ci.cancel();
            return;
        }
        if (ConfigManager.get().inventory.shardFusionHelper && QCloudyAdditionClient.matchesChord(
                QCloudyAdditionClient.ChordAction.OPEN_SHARD_FUSION, event)) {
            QCloudyAdditionClient.openShardFusionGuide(minecraft, null, "");
            ci.cancel();
        }
    }
}
