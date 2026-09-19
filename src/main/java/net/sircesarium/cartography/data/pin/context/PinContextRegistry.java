package net.sircesarium.cartography.data.pin.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PinContextRegistry {
    private static final Map<String, PinDataContext> CONTEXTS = new HashMap<>();
    private static final Map<String, Map<String, GetterEntry>> GETTERS = new HashMap<>();

    static {
        register(new MobContext());
        register(new ItemContext());
        register(new BlockContext());
        register(new PlayerContext());
    }

    public static void register(PinDataContext context) {
        String name = context.getContextName();
        CONTEXTS.put(name, context);

        Map<String, GetterEntry> contextGetters = new HashMap<>();

        for (Method method : context.getClass().getMethods()) {
            PinGetter annotation = method.getAnnotation(PinGetter.class);

            if (annotation != null) {
                contextGetters.put(annotation.value(), new GetterEntry(method, annotation));
            }
        }
        GETTERS.put(name, contextGetters);
    }

    public static PinDataContext get(String name) {
        PinDataContext context = CONTEXTS.get(name);

        if (context == null) {
            throw new IllegalArgumentException("Unknown pin context: '" + name + "'");
        }

        return context;
    }

    public static Map<String, GetterEntry> getGetters(String contextName) {
        Map<String, GetterEntry> getters = GETTERS.get(contextName);

        if (getters == null) {
            throw new IllegalArgumentException("Unknown pin context: '" + contextName + "'");
        }

        return getters;
    }

    public static boolean hasContext(String name) {
        return CONTEXTS.containsKey(name);
    }

    public static GetterEntry getGetter(String contextName, String getterName) {
        Map<String, GetterEntry> getters = getGetters(contextName);
        GetterEntry entry = getters.get(getterName);

        if (entry == null) {
            throw new IllegalArgumentException(
                    "Unknown getter '" + getterName + "' for context '" + contextName + "'"
            );
        }
        
        return entry;
    }

    public record GetterEntry(Method method, PinGetter annotation) {
        public Object invoke(Object contextInstance, Object... args) {
            try {
                return method.invoke(contextInstance, args);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to invoke getter '" + annotation.value() + "' on " + contextInstance.getClass().getSimpleName(),
                        e
                );
            }
        }
    }
}
