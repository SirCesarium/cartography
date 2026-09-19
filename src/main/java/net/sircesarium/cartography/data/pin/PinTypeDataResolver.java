package net.sircesarium.cartography.data.pin;

import net.sircesarium.cartography.data.pin.context.PinContextRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PinTypeDataResolver {

    public static Map<String, Object> resolve(PinType pinType, Object context) {
        String contextName = pinType.type();
        Map<String, Object> result = new HashMap<>();

        for (String getterName : pinType.data()) {
            PinContextRegistry.GetterEntry entry = PinContextRegistry.getGetter(contextName, getterName);
            Object value = entry.invoke(PinContextRegistry.get(contextName), context);
            result.put(getterName, value);
        }

        return result;
    }

    public static void validatePinData(String pinType, List<String> data) {
        if (data.isEmpty()) {
            return;
        }

        if ("dummy".equals(pinType)) {
            throw new IllegalArgumentException("Dummy pin type cannot have data");
        }

        for (String getterName : data) {
            PinContextRegistry.getGetter(pinType, getterName);
        }
    }
}
