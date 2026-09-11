package net.sircesarium.cartography.client.mixin;

import net.minecraft.client.KeyboardHandler;
import net.sircesarium.cartography.config.CartographyServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public class DebugKeyMixin {

    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void cartography$intercept(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 67 && CartographyServerConfig.isRestricted(CartographyServerConfig.disableF3C))
            cir.setReturnValue(true);
        if (key == 71 && CartographyServerConfig.isRestricted(CartographyServerConfig.disableF3G))
            cir.setReturnValue(true);
    }
}
