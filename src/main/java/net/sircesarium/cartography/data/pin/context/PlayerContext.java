package net.sircesarium.cartography.data.pin.context;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@PinContext("player")
public class PlayerContext implements PinDataContext {

    @Override
    public String getContextName() {
        return "player";
    }

    private Player requirePlayer(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }
        
        throw new IllegalArgumentException(
                "player context requires a Player, got: " + entity.getClass().getSimpleName()
        );
    }

    @PinGetter(value = "name", type = "string", description = "Player username")
    public String name(Entity entity) {
        return requirePlayer(entity).getGameProfile().getName();
    }

    @PinGetter(value = "experienceLevel", type = "int", description = "Experience level")
    public int experienceLevel(Entity entity) {
        return requirePlayer(entity).experienceLevel;
    }

    @PinGetter(value = "experienceProgress", type = "float", description = "Progress to next level (0.0-1.0)")
    public float experienceProgress(Entity entity) {
        return requirePlayer(entity).experienceProgress;
    }

    @PinGetter(value = "foodLevel", type = "int", description = "Food level (0-20)")
    public int foodLevel(Entity entity) {
        return requirePlayer(entity).getFoodData().getFoodLevel();
    }

    @PinGetter(value = "saturation", type = "float", description = "Saturation level")
    public float saturation(Entity entity) {
        return requirePlayer(entity).getFoodData().getSaturationLevel();
    }

    @PinGetter(value = "isFlying", type = "boolean", description = "Whether the player is flying")
    public boolean isFlying(Entity entity) {
        return requirePlayer(entity).getAbilities().flying;
    }

    @PinGetter(value = "isCreative", type = "boolean", description = "Whether the player is in creative mode")
    public boolean isCreative(Entity entity) {
        return requirePlayer(entity).isCreative();
    }

    @PinGetter(value = "isSpectator", type = "boolean", description = "Whether the player is in spectator mode")
    public boolean isSpectator(Entity entity) {
        return requirePlayer(entity).isSpectator();
    }

    @PinGetter(value = "isSleeping", type = "boolean", description = "Whether the player is sleeping")
    public boolean isSleeping(Entity entity) {
        return requirePlayer(entity).isSleeping();
    }

    @PinGetter(value = "isSwimming", type = "boolean", description = "Whether the player is swimming")
    public boolean isSwimming(Entity entity) {
        return requirePlayer(entity).isSwimming();
    }

    @PinGetter(value = "isScoping", type = "boolean", description = "Whether the player is using a spyglass")
    public boolean isScoping(Entity entity) {
        return requirePlayer(entity).isScoping();
    }

    @PinGetter(value = "health", type = "float", description = "Current health")
    public float health(Entity entity) {
        return requirePlayer(entity).getHealth();
    }

    @PinGetter(value = "maxHealth", type = "float", description = "Maximum health")
    public float maxHealth(Entity entity) {
        return requirePlayer(entity).getMaxHealth();
    }
}
