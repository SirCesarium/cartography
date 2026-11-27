package net.sircesarium.cartography.client.pin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.data.pin.PinState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class PinStateManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static Map<ResourceLocation, List<PinState>> states = Map.of();

    public PinStateManager() {
        super(GSON, "pin_states");
    }

    @Override
    protected void apply(@Nullable Map<ResourceLocation, JsonElement> elements, @Nullable ResourceManager rm, @Nullable ProfilerFiller profiler) {
        if (elements == null) return;

        Map<ResourceLocation, List<PinState>> result = new HashMap<>();

        for (var entry : elements.entrySet()) {
            try {
                List<PinState> parsed = parseStates(entry.getValue());
                result.put(entry.getKey(), parsed);
                Cartography.LOGGER.info("Loaded pin state: {} ({} states)", entry.getKey(), parsed.size());
            } catch (Exception e) {
                Cartography.LOGGER.warn("Failed to parse pin state {}: ", entry.getKey(), e);
            }
        }

        states = result;
        Cartography.LOGGER.info("Loaded {} pin states", states.size());
    }

    private List<PinState> parseStates(JsonElement element) {
        if (element.isJsonArray()) {
            List<PinState> list = new ArrayList<>();

            for (JsonElement e : element.getAsJsonArray()) {
                list.add(decodeSingle(e));
            }

            return list;
        }

        return List.of(decodeSingle(element));
    }

    private PinState decodeSingle(JsonElement element) {
        return PinState.CODEC
                .parse(JsonOps.INSTANCE, element)
                .getOrThrow(error -> new RuntimeException("Failed to decode pin state: " + error));
    }

    public static List<PinState> getStates(ResourceLocation pinTypeId) {
        return states.getOrDefault(pinTypeId, List.of());
    }

    public static PinState resolveState(ResourceLocation pinTypeId, Map<String, Object> pinData) {
        List<PinState> all = getStates(pinTypeId);

        if (all.isEmpty()) return null;
        if (all.size() == 1) return all.getFirst();

        for (PinState state : all) {
            if (state.condition().isEmpty()) return state;
            if (evaluateCondition(state.condition().get(), pinData)) return state;
        }

        return all.getFirst();
    }

    private static boolean evaluateCondition(String condition, Map<String, Object> pinData) {
        for (String expr : condition.split("&&")) {
            if (!evaluateSingle(expr.trim(), pinData)) return false;
        }

        return true;
    }

    private static boolean evaluateSingle(String expr, Map<String, Object> pinData) {
        String[] parts = splitOperator(expr);
        if (parts == null) return false;

        String key = parts[0].trim();
        String op = parts[1].trim();
        String expected = parts[2].trim();
        Object actual = pinData.get(key);

        if (actual == null) return false;

        return switch (op) {
            case "==" -> equals(actual, expected);
            case "!=" -> !equals(actual, expected);
            case ">" -> compare(actual, expected) > 0;
            case ">=" -> compare(actual, expected) >= 0;
            case "<" -> compare(actual, expected) < 0;
            case "<=" -> compare(actual, expected) <= 0;
            default -> false;
        };
    }

    private static String[] splitOperator(String expr) {
        for (String op : new String[]{">=", "<=", "!=", "==", ">", "<"}) {
            int idx = expr.indexOf(op);

            if (idx != -1) {
                return new String[]{expr.substring(0, idx), op, expr.substring(idx + op.length())};
            }
        }

        return null;
    }

    private static boolean equals(Object actual, String expected) {
        return switch (expected) {
            case "true" -> Boolean.TRUE.equals(actual);
            case "false" -> Boolean.FALSE.equals(actual);
            default -> expected.equals(String.valueOf(actual));
        };
    }

    private static int compare(Object actual, String expected) {
        try {
            double a = Double.parseDouble(String.valueOf(actual));
            double e = Double.parseDouble(expected);
            
            return Double.compare(a, e);
        } catch (NumberFormatException e) {
            return String.valueOf(actual).compareTo(expected);
        }
    }
}
