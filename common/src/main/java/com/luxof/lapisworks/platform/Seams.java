package com.luxof.lapisworks.platform;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Resolves a platform service implementation via {@link ServiceLoader}.
 * <p>
 * Deliberately not a static {@code install()} call: shared code can be reached from mixins and
 * static initializers that may run before a platform entrypoint gets a chance to install anything,
 * so resolving from the classpath removes any initialization-order hazard.
 */
public final class Seams {
    private Seams() {}

    public static <T> T load(Class<T> service) {
        List<ServiceLoader.Provider<T>> providers = ServiceLoader.load(service).stream().toList();
        if (providers.size() != 1) {
            throw new IllegalStateException(
                "There should be exactly one " + service.getName() + " implementation on the "
                    + "classpath. Found: " + providers.stream().map(p -> p.type().getName()).toList()
            );
        }
        return providers.get(0).get();
    }
}
