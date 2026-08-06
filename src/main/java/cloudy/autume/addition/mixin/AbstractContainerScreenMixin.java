package cloudy.autume.addition.mixin;

import cloudy.autume.addition.inventory.SlotLockManager;
import cloudy.autume.addition.inventory.MiddleClickMenus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractContainerScreen.class, priority = 980)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {
    @Shadow @Final protected T menu;
    @Shadow protected Slot hoveredSlot;
    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void aca$protectSlotInteraction(Slot slot, int slotId, int button, ContainerInput action, CallbackInfo ci) {
        var screen = (AbstractContainerScreen<?>) (Object) this;
        if (MiddleClickMenus.convert(screen, slot, button, action)) {
            ci.cancel();
            return;
        }
        if (SlotLockManager.handleBoundSlotClick(screen, slot, action)) {
            ci.cancel();
            return;
        }
        if (slotId == -999 && action == ContainerInput.PICKUP
                && SlotLockManager.shouldBlock(screen, null, ContainerInput.THROW, menu.getCarried(), false)) {
            ci.cancel();
            return;
        }
        if (SlotLockManager.shouldBlock(screen, slot, action, null, false)) {
            ci.cancel();
            return;
        }
        if (action == ContainerInput.SWAP && button >= 0 && button < 9) {
            Slot target = null;
            for (Slot candidate : menu.slots) {
                if (candidate.container instanceof Inventory && candidate.getContainerSlot() == button) {
                    target = candidate;
                    break;
                }
            }
            if (SlotLockManager.shouldBlock(screen, target, action, null, false)) ci.cancel();
        }
    }

    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void aca$renderLockOverlay(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY,
                                       CallbackInfo ci) {
        SlotLockManager.renderSlotOverlay(graphics, slot);
    }

    @Inject(method = "extractContents", at = @At("TAIL"))
    private void aca$renderBindingLines(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta,
                                        CallbackInfo ci) {
        SlotLockManager.renderBindingLines(graphics, (AbstractContainerScreen<?>) (Object) this,
                hoveredSlot, leftPos, topPos);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void aca$inventoryKeys(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (SlotLockManager.keyPressed((AbstractContainerScreen<?>) (Object) this, hoveredSlot, event)) {
            cir.setReturnValue(true);
        }
    }

}
