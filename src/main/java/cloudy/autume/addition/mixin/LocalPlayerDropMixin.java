package cloudy.autume.addition.mixin;

import cloudy.autume.addition.inventory.SlotLockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayer.class, priority = 980)
public abstract class LocalPlayerDropMixin {
    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void aca$protectSelectedItem(boolean all, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        Slot slot = new Slot(player.getInventory(), player.getInventory().getSelectedSlot(), 0, 0);
        var screen = Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?> handled
                ? handled : null;
        if (SlotLockManager.shouldBlock(screen, slot, ContainerInput.THROW, null, true)) cir.setReturnValue(false);
    }
}
