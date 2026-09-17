package net.sircesarium.cartography.helpers;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.sircesarium.cartography.config.F3Restriction;

public class F3RestrictionHelper {

    public static boolean isRestricted(ModConfigSpec.EnumValue<F3Restriction> setting) {
        F3Restriction r = setting.get();
        if (r == F3Restriction.DISABLED) return false;

        var player = Minecraft.getInstance().player;
        if (player == null) return false;

        boolean isOp = player.hasPermissions(2);

        return r == F3Restriction.ALL || (r == F3Restriction.NON_OPS && !isOp);
    }
}
