package cloudy.autume.addition.mixin;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.chat.ChatPeekManager;
import cloudy.autume.addition.config.ConfigManager;
import cloudy.autume.addition.config.ConfigScreen;
import cloudy.autume.addition.inventory.CursorPositionSaver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class, priority = 950)
public abstract class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private double xpos;
    @Shadow private double ypos;

    @Inject(method = "grabMouse", at = @At("HEAD"))
    private void aca$captureOriginalCursor(CallbackInfo ci) {
        CursorPositionSaver.captureOriginal(xpos, ypos);
    }

    @Inject(method = "grabMouse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", ordinal = 2))
    private void aca$captureCenteredCursor(CallbackInfo ci) {
        CursorPositionSaver.captureCentered(xpos, ypos);
    }

    @Inject(method = "releaseMouse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", ordinal = 2))
    private void aca$restoreCursor(CallbackInfo ci) {
        var restored = CursorPositionSaver.restore(xpos, ypos);
        if (restored != null) {
            xpos = restored.x();
            ypos = restored.y();
        }
    }

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void aca$handleMouseChords(long window, MouseButtonInfo button, int action, CallbackInfo ci) {
        MouseButtonEvent event = new MouseButtonEvent(xpos, ypos, button);
        if (action != GLFW.GLFW_PRESS || minecraft.screen != null || minecraft.getOverlay() != null) return;
        if (QCloudyAdditionClient.matchesMouseChord(
                QCloudyAdditionClient.ChordAction.OPEN_CONFIG, event)) {
            minecraft.setScreen(new ConfigScreen(null));
            ci.cancel();
            return;
        }
        if (ConfigManager.get().inventory.shardFusionHelper && QCloudyAdditionClient.matchesMouseChord(
                QCloudyAdditionClient.ChordAction.OPEN_SHARD_FUSION, event)) {
            QCloudyAdditionClient.openShardFusionGuide(minecraft, null, "");
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedSlot()I"), cancellable = true)
    private void aca$keepHotbarStillWhileScrollingChat(long window, double horizontal, double vertical,
                                                        CallbackInfo ci) {
        if (ChatPeekManager.scrollsChat()) ci.cancel();
    }

    @ModifyVariable(method = "onScroll", at = @At(value = "STORE"), name = "wheel")
    private int aca$scrollPeekedChat(int wheel) {
        if (ChatPeekManager.scrollsChat()) Minecraft.getInstance().gui.getChat().scrollChat(wheel);
        return wheel;
    }
}
