package cloudy.autume.addition.mixin.network;

import cloudy.autume.addition.i18n.ModText;
import cloudy.autume.addition.network.ManualReconnectManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DisconnectedScreen.class, priority = 900)
public abstract class DisconnectedScreenMixin extends Screen {
    @Shadow @Final private Screen parent;
    @Shadow @Final private LinearLayout layout;

    protected DisconnectedScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/LinearLayout;arrangeElements()V"))
    private void qca$addManualReconnectButton(CallbackInfo ci) {
        if (!ManualReconnectManager.available()) return;
        layout.addChild(Button.builder(ModText.component("disconnect.reconnect"),
                button -> ManualReconnectManager.reconnect(parent)).width(200).build());
    }
}
