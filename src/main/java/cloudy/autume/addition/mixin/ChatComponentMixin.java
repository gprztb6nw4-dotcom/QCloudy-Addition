package cloudy.autume.addition.mixin;

import cloudy.autume.addition.chat.ChatPeekManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.components.ChatComponent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChatComponent.class, priority = 950)
public abstract class ChatComponentMixin {
    @ModifyVariable(
            method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent$DisplayMode;showRestrictedPrompt:Z",
                    opcode = Opcodes.GETFIELD),
            name = "isForeground"
    )
    private boolean aca$showPeekedChat(boolean foreground) {
        return foreground || ChatPeekManager.active();
    }

    @ModifyExpressionValue(
            method = "getHeight()I",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z")
    )
    private boolean aca$useFocusedChatHeight(boolean focused) {
        return focused || ChatPeekManager.active();
    }
}
