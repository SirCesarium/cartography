package net.sircesarium.cartography.data.pin.context;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Map;

@PinContext("item")
public class ItemContext implements PinDataContext {

    @Override
    public String getContextName() {
        return "item";
    }

    @PinGetter(value = "resourceLocation", type = "string", description = "Item registry name")
    public String resourceLocation(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
    }

    @PinGetter(value = "model", type = "resource_location", description = "Item model resource location")
    public String model(ItemStack item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());

        return "minecraft:item/" + id.getPath();
    }

    @PinGetter(value = "texture", type = "resource_location", description = "Item texture resource location")
    public String texture(ItemStack item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item.getItem());

        return "minecraft:textures/item/" + id.getPath() + ".png";
    }

    @PinGetter(value = "rarity", type = "object", description = "Item rarity with color")
    public Map<String, Object> rarity(ItemStack item) {
        Rarity rarity = item.getRarity();
        Style styled = rarity.getStyleModifier().apply(Style.EMPTY);
        String color = styled.getColor() != null ? styled.getColor().serialize() : "white";

        return Map.of(
                "name", rarity.name(),
                "color", color
        );
    }

    @PinGetter(value = "count", type = "int", description = "Stack count")
    public int count(ItemStack item) {
        return item.getCount();
    }

    @PinGetter(value = "maxStackSize", type = "int", description = "Maximum stack size")
    public int maxStackSize(ItemStack item) {
        return item.getMaxStackSize();
    }

    @PinGetter(value = "isStackable", type = "boolean", description = "Whether the item can be stacked")
    public boolean isStackable(ItemStack item) {
        return item.isStackable();
    }

    @PinGetter(value = "isDamageable", type = "boolean", description = "Whether the item has durability")
    public boolean isDamageable(ItemStack item) {
        return item.isDamageableItem();
    }

    @PinGetter(value = "isDamaged", type = "boolean", description = "Whether the item is damaged")
    public boolean isDamaged(ItemStack item) {
        return item.isDamaged();
    }

    @PinGetter(value = "damage", type = "int", description = "Current damage value")
    public int damage(ItemStack item) {
        return item.getDamageValue();
    }

    @PinGetter(value = "maxDamage", type = "int", description = "Maximum damage value")
    public int maxDamage(ItemStack item) {
        return item.getMaxDamage();
    }

    @PinGetter(value = "isEnchanted", type = "boolean", description = "Whether the item has enchantments")
    public boolean isEnchanted(ItemStack item) {
        return item.isEnchanted();
    }

    @PinGetter(value = "displayName", type = "string", description = "Display name of the item")
    public String displayName(ItemStack item) {
        return item.getHoverName().getString();
    }

    @PinGetter(value = "isFood", type = "boolean", description = "Whether the item is food")
    public boolean isFood(ItemStack item) {
        return item.has(DataComponents.FOOD);
    }
}
