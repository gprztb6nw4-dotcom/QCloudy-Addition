package cloudy.autume.addition.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("hoveredSlot")
    @Nullable Slot aca$getHoveredSlot();

    @Accessor("leftPos")
    int aca$getLeftPos();

    @Accessor("topPos")
    int aca$getTopPos();
}
