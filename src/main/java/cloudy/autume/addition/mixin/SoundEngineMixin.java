package cloudy.autume.addition.mixin;

import cloudy.autume.addition.combat.DeployableExpiryAlert;
import cloudy.autume.addition.hunting.BeeheemothSoundCustomizer;
import cloudy.autume.addition.inventory.TeleportSoundCustomizer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundEngine.class, priority = 950)
public abstract class SoundEngineMixin {
    @ModifyVariable(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getIdentifier()Lnet/minecraft/resources/Identifier;"),
            argsOnly = true, ordinal = 0)
    private SoundInstance aca$customizeBeeheemothSound(SoundInstance sound) {
        DeployableExpiryAlert.onSound(sound);
        return BeeheemothSoundCustomizer.customize(sound);
    }

    @Inject(method = "play", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/sounds/SoundInstance;getIdentifier()Lnet/minecraft/resources/Identifier;"),
            cancellable = true)
    private void aca$customizeTeleportSwordSound(SoundInstance sound,
                                                  CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (TeleportSoundCustomizer.customize(sound)) cir.cancel();
    }
}
