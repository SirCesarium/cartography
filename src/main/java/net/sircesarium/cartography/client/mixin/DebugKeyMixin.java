package net.sircesarium.cartography.client.mixin;

import java.util.Map;

import net.minecraft.client.KeyboardHandler;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.sircesarium.cartography.config.CartographyServerConfig;
import net.sircesarium.cartography.config.F3Restriction;
import net.sircesarium.cartography.helpers.F3RestrictionHelper;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(KeyboardHandler.class)
public class DebugKeyMixin {

    private static final Map<Integer, ModConfigSpec.EnumValue<F3Restriction>> DISABLED_KEYS = Map.of(
            GLFW.GLFW_KEY_C, CartographyServerConfig.disableF3C,
            GLFW.GLFW_KEY_G, CartographyServerConfig.disableF3G
    );

    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void cartography$intercept(int key, CallbackInfoReturnable<Boolean> cir) {
        var setting = DISABLED_KEYS.get(key);

        if (setting != null && F3RestrictionHelper.isRestricted(setting))
            cir.setReturnValue(true);
    }
}
