package net.sircesarium.cartography.data.pin.context;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;

@PinContext("mob")
public class MobContext implements PinDataContext {

    public static final TagKey<EntityType<?>> VEHICLES =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("cartography", "vehicles"));

    @Override
    public String getContextName() {
        return "mob";
    }

    private LivingEntity requireLiving(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living;
        }

        throw new IllegalArgumentException(
                "mob context requires a LivingEntity, got: " + entity.getClass().getSimpleName()
        );
    }

    @PinGetter(value = "isHostile", type = "boolean", description = "Whether the mob is hostile to players")
    public boolean isHostile(Entity entity) {
        return entity instanceof Monster;
    }

    @PinGetter(value = "type", type = "string", description = "Entity type registry name")
    public String type(Entity entity) {
        return entity.getType().toShortString();
    }

    @PinGetter(value = "name", type = "string", description = "Display name of the entity")
    public String name(Entity entity) {
        return entity.getDisplayName().getString();
    }

    @PinGetter(value = "isTamed", type = "boolean", description = "Whether the mob is tamed")
    public boolean isTamed(Entity entity) {
        if (entity instanceof TamableAnimal tamable) {
            return tamable.isTame();
        }

        return false;
    }

    @PinGetter(value = "health", type = "float", description = "Current health of the entity")
    public float health(Entity entity) {
        return requireLiving(entity).getHealth();
    }

    @PinGetter(value = "maxHealth", type = "float", description = "Maximum health of the entity")
    public float maxHealth(Entity entity) {
        return requireLiving(entity).getMaxHealth();
    }

    @PinGetter(value = "isBaby", type = "boolean", description = "Whether the entity is a baby")
    public boolean isBaby(Entity entity) {
        return requireLiving(entity).isBaby();
    }

    @PinGetter(value = "armor", type = "int", description = "Armor value of the entity")
    public int armor(Entity entity) {
        return requireLiving(entity).getArmorValue();
    }

    @PinGetter(value = "isLeashed", type = "boolean", description = "Whether the entity is leashed")
    public boolean isLeashed(Entity entity) {
        if (entity instanceof Leashable leashable) {
            return leashable.isLeashed();
        }
        return false;
    }

    @PinGetter(value = "isSleeping", type = "boolean", description = "Whether the entity is sleeping")
    public boolean isSleeping(Entity entity) {
        return requireLiving(entity).isSleeping();
    }

    @PinGetter(value = "isSitting", type = "boolean", description = "Whether the entity is sitting (tamed only)")
    public boolean isSitting(Entity entity) {
        if (entity instanceof TamableAnimal tamable) {
            return tamable.isInSittingPose();
        }
        return false;
    }

    @PinGetter(value = "hasEffects", type = "boolean", description = "Whether the entity has active potion effects")
    public boolean hasEffects(Entity entity) {
        return !requireLiving(entity).getActiveEffects().isEmpty();
    }

    @PinGetter(value = "effectCount", type = "int", description = "Number of active potion effects")
    public int effectCount(Entity entity) {
        return requireLiving(entity).getActiveEffects().size();
    }

    @PinGetter(value = "isOnFire", type = "boolean", description = "Whether the entity is on fire")
    public boolean isOnFire(Entity entity) {
        return entity.isOnFire();
    }

    @PinGetter(value = "isInWater", type = "boolean", description = "Whether the entity is in water")
    public boolean isInWater(Entity entity) {
        return entity.isInWater();
    }

    @PinGetter(value = "isNoAi", type = "boolean", description = "Whether the entity has AI disabled")
    public boolean isNoAi(Entity entity) {
        if (entity instanceof Mob mob) {
            return mob.isNoAi();
        }
        return false;
    }

    @PinGetter(value = "isVehicle", type = "boolean", description = "Whether the entity is a vehicle (boat, minecart)")
    public boolean isVehicle(Entity entity) {
        return entity.getType().is(VEHICLES);
    }
}
