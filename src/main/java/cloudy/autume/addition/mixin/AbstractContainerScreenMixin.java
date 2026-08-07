package cloudy.autume.addition.mixin;

import cloudy.autume.addition.inventory.MiddleClickMenus;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class, priority = 980)
public abstract class AbstractContainerScreenMixin {
    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void aca$convertMenuClick(Slot slot, int slotId, int button, ContainerInput action, CallbackInfo ci) {
        var screen = (AbstractContainerScreen<?>) (Object) this;
        if (MiddleClickMenus.convert(screen, slot, button, action)) {
            ci.cancel();
        }
    }

}
