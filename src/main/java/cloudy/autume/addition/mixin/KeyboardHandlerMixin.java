package cloudy.autume.addition.mixin;

import cloudy.autume.addition.QCloudyAdditionClient;
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
        if (action != org.lwjgl.glfw.GLFW.GLFW_PRESS || minecraft.screen != null) return;
        if (QCloudyAdditionClient.matchesChord(
                QCloudyAdditionClient.ChordAction.OPEN_CONFIG, event)) {
            minecraft.setScreen(new ConfigScreen(null));
            ci.cancel();
        }
    }
}
